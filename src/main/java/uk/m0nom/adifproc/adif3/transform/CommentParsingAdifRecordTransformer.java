package uk.m0nom.adifproc.adif3.transform;

import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Band;
import org.marsik.ham.adif.enums.Propagation;
import org.marsik.ham.adif.types.Pota;
import org.marsik.ham.adif.types.PotaList;
import org.marsik.ham.adif.types.Sota;
import org.marsik.ham.adif.types.Wwff;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityDatabase;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.contacts.Qsos;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.adif3.transform.comment.CommentTransformer;
import uk.m0nom.adifproc.adif3.transform.comment.FieldParserCommentTransformer;
import uk.m0nom.adifproc.adif3.transform.comment.SchemaBasedCommentTransformer;
import uk.m0nom.adifproc.adif3.transform.comment.parsers.FieldParseResult;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.coords.LocationAccuracy;
import uk.m0nom.adifproc.coords.LocationSource;
import uk.m0nom.adifproc.geocoding.GeocodingProvider;
import uk.m0nom.adifproc.geocoding.GeocodingResult;
import uk.m0nom.adifproc.geocoding.NominatimGeocodingProvider;
import uk.m0nom.adifproc.location.FromLocationDeterminer;
import uk.m0nom.adifproc.location.ToLocationDeterminer;
import uk.m0nom.adifproc.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.adifproc.qrz.CachingQrzXmlService;
import uk.m0nom.adifproc.qrz.QrzCallsign;
import uk.m0nom.adifproc.satellite.ApSatellite;
import uk.m0nom.adifproc.satellite.ApSatelliteService;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import static uk.m0nom.adifproc.adif3.transform.AdifQrzEnricher.getNameFromQrzData;

@Service
public class CommentParsingAdifRecordTransformer implements Adif3RecordTransformer {
    private static final Logger logger = Logger.getLogger(CommentParsingAdifRecordTransformer.class.getName());

    private final ActivityDatabaseService activities;
    private final CachingQrzXmlService qrzXmlService;
    private final AdifQrzEnricher enricher;
    private final FromLocationDeterminer fromLocationDeterminer;
    private final ToLocationDeterminer toLocationDeterminer;
    private final ActivityProcessor activityProcessor;
    private final GeocodingProvider geocodingProvider;
    private final CommentTransformer fieldParserCommentTransformer;
    private final CommentTransformer schemaBasedCommentTransformer;
    private final ApSatelliteService apSatelliteService;


    public CommentParsingAdifRecordTransformer(ActivityDatabaseService activities,
                                               CachingQrzXmlService qrzXmlService,
                                               SchemaBasedCommentTransformer schemaBasedCommentTransformer,
                                               FieldParserCommentTransformer fieldParserCommentTransformer,
                                               FromLocationDeterminer fromLocationDeterminer,
                                               ToLocationDeterminer toLocationDeterminer,
                                               ActivityProcessor activityProcessor,
                                               NominatimGeocodingProvider geocodingProvider,
                                               ApSatelliteService apSatelliteService) {
        this.activities = activities;
        this.qrzXmlService = qrzXmlService;
        this.enricher = new AdifQrzEnricher(qrzXmlService);
        this.apSatelliteService = apSatelliteService;
        this.fromLocationDeterminer = fromLocationDeterminer;
        this.toLocationDeterminer = toLocationDeterminer;
        this.activityProcessor = activityProcessor;
        this.geocodingProvider = geocodingProvider;
        this.fieldParserCommentTransformer = fieldParserCommentTransformer;
        this.schemaBasedCommentTransformer = schemaBasedCommentTransformer;
    }

    private void processSotaRef(Qso qso, TransformResults results) {
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

    private void processWwffRef(Qso qso, TransformResults results) {
        Adif3Record rec = qso.getRecord();

        if (rec.getWwffRef() != null && StringUtils.isNotBlank(rec.getWwffRef().getValue())) {
            String wwffId = rec.getWwffRef().getValue();
            Activity activity = activities.getDatabase(ActivityType.WWFF).get(wwffId);
            if (activity != null) {
                qso.getTo().addActivity(activity);
                String result = toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.WWFF, wwffId);
                if (result != null) {
                    results.addContactWithDubiousLocation(result);
                }
            } else {
                results.addContactWithDubiousLocation(String.format("%s (WWFF %s invalid)", qso.getTo().getCallsign(), wwffId));
            }
        }
    }

    private void processPotaRefs(Qso qso, TransformResults results) {
        boolean locationSet = false;
        Adif3Record rec = qso.getRecord();

        if (rec.getPotaRef() != null && StringUtils.isNotBlank(rec.getPotaRef().getValue())) {
            PotaList potaIds = rec.getPotaRef();
            for (Pota potaId : potaIds.getPotaList()) {
                Activity activity = activities.getDatabase(ActivityType.POTA).get(potaId.getValue());
                if (activity != null) {
                    qso.getTo().addActivity(activity);
                    if (!locationSet) {
                        String result = toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.POTA, potaId.getValue());
                        if (result != null) {
                            results.addContactWithDubiousLocation(result);
                        } else {
                            locationSet = true;
                        }
                    }
                } else {
                    results.addContactWithDubiousLocation(String.format("%s (POTA %s invalid)", qso.getTo().getCallsign(), potaId.getValue()));
                }
            }
            if (potaIds.getPotaList().size() > 1) {
                results.addContactWithDubiousLocation(String.format("Multiple POTA Ids: %s, using first ref as location", potaIds.getValue()));
            }
        }
    }

    private void processRailwaysOnTheAirCallsign(Qso qso, TransformResults results) {
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

    private void setMyInfoFromQrz(TransformControl control, Qso qso) {
        // Attempt a lookup from QRZ.com
        QrzCallsign myQrzData = qrzXmlService.getCallsignData(qso.getRecord().getStationCallsign());

        fromLocationDeterminer.setMyLocation(control, qso, myQrzData);

        qso.getFrom().setQrzInfo(myQrzData);
        enricher.enrichAdifForMe(qso.getRecord(), myQrzData);
    }

    private QrzCallsign setTheirInfoFromQrz(TransformResults transformResults, Qso qso) {
        /* Load QRZ.COM info for the worked station as a fixed station, for information */
        QrzCallsign theirQrzData = qrzXmlService.getCallsignData(qso.getTo().getCallsign());
        qso.getTo().setQrzInfo(theirQrzData);
        enricher.enrichAdifForThem(transformResults, qso.getRecord(), theirQrzData);
        return theirQrzData;
    }

    private void processSatelliteInfo(TransformControl control, Qso qso) {
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

    private boolean hasValidGridSquareNoCoords(Adif3Record rec) {
        return rec.getCoordinates() == null && rec.getGridsquare() != null &&
                !MaidenheadLocatorConversion.isADubiousGridSquare(rec.getGridsquare());
    }

    private boolean setCoordinatesFromGridSquare(Qso qso) {
        Adif3Record rec = qso.getRecord();
        // Set Coordinates from GridSquare that has been supplied in the input file
        try {
            // Only set the gridsquare if it is a valid maidenhead locator
            GlobalCoords3D coords = MaidenheadLocatorConversion.locatorToCoords(LocationSource.OVERRIDE, rec.getGridsquare(), rec.getGridsquareExt());
            rec.setCoordinates(coords);
            qso.getTo().setCoordinates(coords);
            qso.getTo().setGrid(rec.getGridsquare());
        } catch (UnsupportedOperationException e) {
            logger.warning(String.format("For QSO with %s: %s", qso.getTo().getCallsign(), e.getMessage()));
            return false;
        }
        return true;
    }

    private boolean hasNoValidGridSquareOrCoords(Adif3Record rec) {
        return rec.getCoordinates() == null || MaidenheadLocatorConversion.isADubiousGridSquare(rec.getGridsquare());
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

    private boolean coordsAreZero(GlobalCoordinates coords) {
        return coords.getLatitude() == 0.0 && coords.getLongitude() == 0.0;
    }

    private void nullCoordsIfZero(Adif3Record rec) {
        if (rec.getCoordinates() != null && coordsAreZero(rec.getCoordinates())) {
            rec.setCoordinates(null);
        }
        if (rec.getMyCoordinates() != null && coordsAreZero(rec.getMyCoordinates())) {
            rec.setMyCoordinates(null);
        }
    }

    @Override
    public void transform(TransformControl control, TransformResults results, Qsos qsos, Adif3Record rec, int index) {
        Map<String, String> unmapped = new HashMap<>();
        results.getSatelliteActivity().setSatellites(apSatelliteService);

        // A HAM Radio Log ADI input file had both my/their coords set to 0/0 - clearly these aren't right!
        nullCoordsIfZero(rec);

        /* Add Adif3Record details to the Qsos meta structure */
        Qso qso = new Qso(rec, index);
        qsos.addQso(qso);

        activityProcessor.processActivities(control, qso.getFrom(), rec);

        qso.getFrom().setAntenna(control.getAntenna());
        setMyInfoFromQrz(control, qso);
        QrzCallsign theirQrzData = setTheirInfoFromQrz(results, qso);

        // We first use the schema based transformer...
        schemaBasedCommentTransformer.transformComment(qso, rec.getComment(), unmapped, results);

        // Any unmapped comments we form into a new comment list
        String remainingComment = generateCommentFromUnmapped(unmapped);
        // Then apply our previous transformer on the remaining comment list for compatibility
        fieldParserCommentTransformer.transformComment(qso, remainingComment, unmapped, results);

        // Now that we've potentially populated the ADIF fields from activities in the comments, check the references
        // and process them.
        processSotaRef(qso, results);
        processWwffRef(qso, results);
        processPotaRefs(qso, results);
        processRailwaysOnTheAirCallsign(qso, results);
        processSatelliteInfo(control, qso);

        if (rec.getCoordinates() == null && rec.getGridsquare() == null) {
            enricher.lookupLocationFromQrz(qso);
        }

        // IF qrz.com can't fill in the coordinates, and the gridsquare is set, fill in coordinates from that
        if (hasValidGridSquareNoCoords(rec)) {
            if (!setCoordinatesFromGridSquare(qso)) {
                results.addContactWithDubiousLocation(String.format("%s (Locator %s invalid)", qso.getTo().getCallsign(), rec.getGridsquare()));
            }
        }

        // Last resort, attempt to find location from qrz.com address data via geolocation provider
        if (hasNoValidGridSquareOrCoords(rec) && theirQrzData != null) {
            setTheirLocationFromGeocodedAddress(qso, theirQrzData);
        }

        improveAccuracyOfMyLocationIfRequired(qso);

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
            if (apSatelliteService.isAKnownSatellite(rec.getSatName())) {
                ApSatellite satellite = apSatelliteService.getSatellite(rec.getSatName());
                if (satellite.isGeostationary() || apSatelliteService.getEarliestDataAvailable().isBefore(rec.getQsoDate())) {
                    results.getSatelliteActivity().recordSatelliteActivity(qso);
                } else {
                    results.addUnknownSatellitePass(String.format("%s: %s", rec.getSatName(), rec.getQsoDate()));
                }
            } else {
                results.addUnknownSatellite(rec.getSatName());
            }
        }

        // Override your altitude if defined
        if (rec.getMyAltitude() != null) {
            double alt = rec.getMyAltitude();
            if (qso.getFrom().getCoordinates() != null) {
                qso.getFrom().getCoordinates().setAltitude(alt);
            }
        }

        // Override their altitude if defined
        if (rec.getAltitude() != null) {
            double alt = rec.getAltitude();
            if (qso.getTo().getCoordinates() != null) {
                qso.getTo().getCoordinates().setAltitude(alt);
            }
        }

        // Set DXCC Entities
        qso.getFrom().setDxccEntity(control.getDxccEntities().findDxccEntityFromCallsign(qso.getFrom().getCallsign(), qso.getRecord().getQsoDate()));
        qso.getTo().setDxccEntity(control.getDxccEntities().findDxccEntityFromCallsign(qso.getTo().getCallsign(), qso.getRecord().getQsoDate()));
        checkForWarnings(results, qso);
    }

    private void checkForWarnings(TransformResults results, Qso qso) {
        QrzCallsign qrzInfo = qso.getTo().getQrzInfo();
        Adif3Record rec = qso.getRecord();

        if (qrzInfo != null) {
            if (rec.getName() != null && qrzInfo.getName() != null) {
                String displayQrzName = getNameFromQrzData(qrzInfo);
                if (!StringUtils.equalsIgnoreCase(rec.getName(), displayQrzName)) {
                    // Warn if other station's name isn't contained in qrz data
                    if (!StringUtils.containsIgnoreCase(displayQrzName, rec.getName())) {
                        results.addWarning(String.format("Check name for %s: provided is '%s', QRZ has '%s'",
                                rec.getCall(), rec.getName(), displayQrzName));
                    }
                }
            }
            if (rec.getState() != null && qrzInfo.getState() != null) {
                // Warn if state specified explicitly doesn't match qrz data and they're not doing an activity
                if (!qso.getTo().hasActivity()) {
                    if (!StringUtils.equalsIgnoreCase(rec.getState(), qrzInfo.getState())) {
                        results.addWarning(String.format("Check state for %s: provided is %s, QRZ has %s", rec.getCall(),
                                rec.getState().toUpperCase(), qrzInfo.getState().toUpperCase()));
                    }
                }
            }
        }
    }

    /**
     * This came about for FT8 contacts where the operators gridsquare is low accuracy but defines
     * where they are, so if they have a contact with another station in that gridsquare you get the
     * sharing location warning.
     *
     * @param qso qso to examine and update
     */
    private void improveAccuracyOfMyLocationIfRequired(Qso qso) {
        // See if we have QRZ.com info for me
        if (qso.getFrom().getQrzInfo() != null) {
            QrzCallsign myQrzInfo = qso.getFrom().getQrzInfo();
            if (myQrzInfo.getGrid() != null) {
                String myQrzGrid = myQrzInfo.getGrid();
                // If the qrz grid info is more accurate replace with that info
                String myQsoGrid = qso.getFrom().getGrid();
                if (myQrzGrid.length() > myQsoGrid.length()) {
                    if (myQrzGrid.startsWith(myQsoGrid)) {
                        GlobalCoords3D coords = MaidenheadLocatorConversion.locatorToCoords(LocationSource.QRZ, myQrzGrid, null);
                        qso.getFrom().setCoordinates(coords);
                        qso.getRecord().setMyCoordinates(coords);
                    }
                }
            }
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

                    boolean sotaFieldEmpty = rec.getSotaRef() == null || StringUtils.isBlank(rec.getSotaRef().getValue());
                    boolean wwffFieldEmpty = rec.getWwffRef() == null || StringUtils.isBlank(rec.getWwffRef().getValue());
                    boolean potaFieldEmpty = rec.getPotaRef() == null || StringUtils.isBlank(rec.getPotaRef().getValue());

                    // Make sure if they have a SOTA, WWFF or POTA reference specified
                    // in their specific fields that they take precedence over any other reference
                    // hence why this code is only executed if these specific references are null
                    if (sotaFieldEmpty && wwffFieldEmpty && potaFieldEmpty) {
                        toLocationDeterminer.setTheirLocationFromActivity(qso, activity);
                    }

                    // If activity in SIG_INFO/SIG_REF is SOTA and SOTA specific field isn't set, set it now
                    if (sotaFieldEmpty && activity.getType() == ActivityType.SOTA) {
                        rec.setSotaRef(Sota.valueOf(activity.getRef()));
                        clearSigAndSigInfo(rec);
                    }
                    // If activity in SIG_INFO/SIG_REF is WWFF and WWFF specific field isn't set, set it now
                    if (wwffFieldEmpty && activity.getType() == ActivityType.WWFF) {
                        rec.setWwffRef(Wwff.valueOf(activity.getRef()));
                        clearSigAndSigInfo(rec);
                    }
                    // If activity in SIG_INFO/SIG_REF is POTA and POTA specific field isn't set, set it now
                    if (potaFieldEmpty && activity.getType() == ActivityType.POTA) {
                        rec.setPotaRef(PotaList.valueOf(activity.getRef()));
                        clearSigAndSigInfo(rec);
                    }
                }
            }
        }
    }

    private void clearSigAndSigInfo(Adif3Record rec) {
        rec.setSig(null);
        rec.setSigInfo(null);
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

    private String generateCommentFromUnmapped(Map<String, String> unmapped) {
        StringBuilder sb = new StringBuilder();
        for (String key : unmapped.keySet()) {
            sb.append(String.format("%s: %s ", key, unmapped.get(key)));
        }
        return sb.toString();
    }
}
