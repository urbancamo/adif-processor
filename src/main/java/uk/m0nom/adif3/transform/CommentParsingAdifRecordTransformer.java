package uk.m0nom.adif3.transform;

import com.amihaiemil.eoyaml.YamlMapping;
import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Band;
import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.adif3.transform.comment.CommentTransformer;
import uk.m0nom.adif3.transform.comment.FieldParserCommentTransformer;
import uk.m0nom.coords.GlobalCoords3D;
import uk.m0nom.coords.LocationSource;
import uk.m0nom.geocoding.GeocodingProvider;
import uk.m0nom.geocoding.GeocodingResult;
import uk.m0nom.geocoding.NominatimGeocodingProvider;
import uk.m0nom.location.FromLocationDeterminer;
import uk.m0nom.location.ToLocationDeterminer;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.qrz.QrzCallsign;
import uk.m0nom.qrz.QrzService;
import uk.m0nom.satellite.ApSatellites;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class CommentParsingAdifRecordTransformer implements Adif3RecordTransformer {
    private static final Logger logger = Logger.getLogger(CommentParsingAdifRecordTransformer.class.getName());

    private final ActivityDatabases activities;
    private final QrzService qrzService;
    private final TransformControl control;
    private final TransformResults results;
    private final AdifQrzEnricher enricher;
    private final FromLocationDeterminer fromLocationDeterminer;
    private final ToLocationDeterminer toLocationDeterminer;
    private final ActivityProcessor activityProcessor;
    private final GeocodingProvider geocodingProvider;
    private Map<String, String> unmapped;
    private final CommentTransformer commentTransformer;

    public CommentParsingAdifRecordTransformer(YamlMapping config,
                                               ActivityDatabases activities,
                                               QrzService qrzService,
                                               TransformControl control,
                                               TransformResults results) {
        this.activities = activities;
        this.qrzService = qrzService;
        this.control = control;
        this.enricher = new AdifQrzEnricher(qrzService);
        ApSatellites apSatellites = new ApSatellites();
        this.results = results;
        results.getSatelliteActivity().setSatellites(apSatellites);
        this.fromLocationDeterminer = new FromLocationDeterminer(control, qrzService, activities);
        this.toLocationDeterminer = new ToLocationDeterminer(control, qrzService, activities);
        this.activityProcessor = new ActivityProcessor(control, qrzService, activities);
        this.geocodingProvider = new NominatimGeocodingProvider();
        this.unmapped = new HashMap<>();
        this.commentTransformer = new FieldParserCommentTransformer(config, activities, toLocationDeterminer, apSatellites);
    }

    private void processSotaRef(Qso qso) {
        Adif3Record rec = qso.getRecord();

        if (rec.getSotaRef() != null && StringUtils.isNotBlank(rec.getSotaRef().getValue())) {
            String sotaId = rec.getSotaRef().getValue();
            Activity activity = activities.getDatabase(ActivityType.SOTA).get(sotaId);
            if (activity != null) {
                qso.getTo().addActivity(activity);
                String result = toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.SOTA, sotaId);
                if (result != null) {
                    results.addContactWithDubiousLocation(result);
                }
            } else {
                results.addContactWithDubiousLocation(String.format("%s (SOTA %s invalid)", qso.getTo().getCallsign(), sotaId));
            }
        }
    }

    private void processRailwaysOnTheAirCallsign(Qso qso) {
        Adif3Record rec = qso.getRecord();
        // Check the callsign for a Railways on the Air
        Activity rotaInfo = activities.getDatabase(ActivityType.ROTA).get(rec.getCall().toUpperCase());
        if (rotaInfo != null) {
            String result = toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.ROTA, rotaInfo.getRef());
            if (result != null) {
                results.addContactWithDubiousLocation(result);
            }
            qso.getTo().addActivity(rotaInfo);
        }
    }

    private void setMyInfoFromQrz(Qso qso) {
        // Attempt a lookup from QRZ.com
        QrzCallsign myQrzData = qrzService.getCallsignData(qso.getRecord().getStationCallsign());

        fromLocationDeterminer.setMyLocation(qso, myQrzData);

        qso.getFrom().setQrzInfo(myQrzData);
        enricher.enrichAdifForMe(qso.getRecord(), myQrzData);
    }

    private QrzCallsign setTheirInfoFromQrz(Qso qso) {
        /* Load QRZ.COM info for the worked station as a fixed station, for information */
        QrzCallsign theirQrzData = qrzService.getCallsignData(qso.getTo().getCallsign());
        qso.getTo().setQrzInfo(theirQrzData);
        enricher.enrichAdifForThem(qso.getRecord(), theirQrzData);
        return theirQrzData;
    }

    private void processSatelliteInfo(Qso qso) {
        Adif3Record rec = qso.getRecord();
        if (StringUtils.isBlank(control.getSatelliteBand()) || rec.getBand() == Band.findByCode(control.getSatelliteBand().toLowerCase())) {
            if (StringUtils.isNotBlank(control.getSatelliteMode())) {
                rec.setSatMode(control.getSatelliteMode().toUpperCase());
            }
            if (StringUtils.isNotBlank(control.getSatelliteName())) {
                rec.setSatName(control.getSatelliteName().toUpperCase());
                // Set Propagation Mode Automagically
                rec.setPropMode(Propagation.SATELLITE);
            }
        }
    }

    private boolean hasValidGridsquareNoCoords(Adif3Record rec) {
        return rec.getCoordinates() == null &&
                MaidenheadLocatorConversion.isAValidGridSquare(rec.getGridsquare()) &&
                !MaidenheadLocatorConversion.isADubiousGridSquare(rec.getGridsquare());
    }

    private boolean setCoordinatesFromGridsquare(Qso qso) {
        Adif3Record rec = qso.getRecord();
        // Set Coordinates from GridSquare that has been supplied in the input file
        try {
            // Only set the gridsquare if it is a valid maidenhead locator
            GlobalCoords3D coords = MaidenheadLocatorConversion.locatorToCoords(LocationSource.OVERRIDE, rec.getGridsquare());
            rec.setCoordinates(coords);
            qso.getTo().setCoordinates(coords);
            qso.getTo().setGrid(rec.getGridsquare());
        } catch (UnsupportedOperationException e) {
            logger.warning(e.getMessage());
            return false;
        }
        return true;
    }

    private boolean hasNoValidGridsquareOrCoords(Adif3Record rec) {
        return rec.getCoordinates() == null &&
                (!MaidenheadLocatorConversion.isAValidGridSquare(rec.getGridsquare()) ||
                        MaidenheadLocatorConversion.isADubiousGridSquare(rec.getGridsquare()));
    }

    private void setTheirLocationFromGeocodedAddress(Qso qso, QrzCallsign theirQrzData) {
        Adif3Record rec = qso.getRecord();
        try {
            GeocodingResult result = geocodingProvider.getLocationFromAddress(theirQrzData);
            rec.setCoordinates(result.getCoordinates());
            qso.getTo().setCoordinates(result.getCoordinates());
            if (rec.getCoordinates() != null) {
                logger.info(String.format("Location for %s set based on geolocation data to: %s", rec.getCall(), rec.getCoordinates()));
                if (MaidenheadLocatorConversion.isEmptyOrInvalid(rec.getGridsquare())) {
                    rec.setGridsquare(MaidenheadLocatorConversion.coordsToLocator(rec.getCoordinates()));
                }
            }
        } catch (Exception e) {
            logger.severe(String.format("Caught Exception from Geolocation Provider looking up %s: %s", rec.getCall(), e.getMessage()));
        }
    }

    @Override
    public void transform(Qsos qsos, Adif3Record rec, int index) {
        unmapped = new HashMap<>();

        /* Add Adif3Record details to the Qsos meta structure */
        Qso qso = new Qso(rec, index);
        qsos.addQso(qso);

        activityProcessor.processActivities(qso.getFrom(), rec);

        setMyInfoFromQrz(qso);
        QrzCallsign theirQrzData = setTheirInfoFromQrz(qso);
        processSotaRef(qso);
        processRailwaysOnTheAirCallsign(qso);
        processSatelliteInfo(qso);
        commentTransformer.transformComment(qso, rec.getComment(), unmapped, results);

        if (rec.getCoordinates() == null && rec.getGridsquare() == null) {
            enricher.lookupLocationFromQrz(qso);
        }

        // IF qrz.com can't fill in the coordinates, and the gridsquare is set, fill in coordinates from that
        if (hasValidGridsquareNoCoords(rec)) {
            if (!setCoordinatesFromGridsquare(qso)) {
                results.addContactWithDubiousLocation(String.format("%s (Locator %s invalid)", qso.getTo().getCallsign(), rec.getGridsquare()));
            }
        }

        // Last resort, attempt to find location from qrz.com address data via geolocation provider
        if (hasNoValidGridsquareOrCoords(rec) && theirQrzData != null) {
            setTheirLocationFromGeocodedAddress(qso, theirQrzData);
        }

        // Look to see if there is anything in the SIG/SIGINFO fields
        if (StringUtils.isNotBlank(rec.getSig())) {
            processSig(qso, unmapped);
        }

        if (control.isStripComment()) {
            if (!unmapped.isEmpty()) {
                addUnmappedToRecord(rec, unmapped);
            } else {
                // done a good job and slotted all the key/value pairs in the right place
                rec.setComment("");
            }
        }

        // Add the SOTA Microwave Award data to the end of the comment field
        if (control.isSotaMicrowaveAwardComment()) {
            SotaMicrowaveAward.addSotaMicrowaveAwardToComment(rec);
        }

        if (rec.getSatName() != null) {
            results.getSatelliteActivity().recordSatelliteActivity(qso);
        }
    }

    private void processSig(Qso qso, Map<String, String> unmapped) {
        Adif3Record rec = qso.getRecord();
        String activityType = rec.getSig().toUpperCase();
        String activityLocation = rec.getSigInfo().toUpperCase();

        if (StringUtils.isNotBlank(activityType)) {
            // See if it is an activity we support
            ActivityDatabase database = activities.getDatabase(activityType);
            if (database != null) {
                Activity activity = database.get(activityLocation);
                if (activity != null) {
                    qso.getTo().addActivity(activity);

                    // Make sure if they have a SOTA reference this takes precedence over any other reference
                    // hence why this code is only executed if the sota ref is null
                    if (rec.getSotaRef() == null || StringUtils.isBlank(rec.getSotaRef().getValue())) {
                        toLocationDeterminer.setTheirLocationFromActivity(qso, activity);
                    }
                    unmapped.put(activityType, activityLocation);
                }
            }
        }
    }

    /**
     * Any key/value pairs in the fast log entry comment string that can't be mapped into a specific ADIF field
     * are added to the comment string in the ADIF file
     *
     * @param rec      ADIF record
     * @param unmapped unmapped parameters
     */
    private void addUnmappedToRecord(Adif3Record rec, Map<String, String> unmapped) {
        StringBuilder sb = new StringBuilder();
        Set<String> keySet = unmapped.keySet();
        int keySetLen = keySet.size();
        int i = 1;
        for (String key : unmapped.keySet()) {
            if (StringUtils.isNotEmpty(key)) {
                sb.append(String.format("%s: %s", key, unmapped.get(key)));
                if (i++ < keySetLen) {
                    sb.append(", ");
                }
            } else {
                sb.append(String.format("%s ", key));
            }
        }
        rec.setComment(sb.toString());
    }
}
