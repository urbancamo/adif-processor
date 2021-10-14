package uk.m0nom.adif3.transform;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.*;
import org.marsik.ham.adif.types.Iota;
import org.marsik.ham.adif.types.Sota;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.adif3.transform.tokenizer.ColonTokenizer;
import uk.m0nom.adif3.transform.tokenizer.CommentTokenizer;
import uk.m0nom.coords.LocationParsers;
import uk.m0nom.geocoding.GeocodingProvider;
import uk.m0nom.geocoding.NominatimGeocodingProvider;
import uk.m0nom.location.FromLocationDeterminer;
import uk.m0nom.location.ToLocationDeterminer;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.qrz.QrzCallsign;
import uk.m0nom.qrz.QrzService;
import uk.m0nom.satellite.Satellites;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

public class CommentParsingAdifRecordTransformer implements Adif3RecordTransformer {
    private static final Logger logger = Logger.getLogger(CommentParsingAdifRecordTransformer.class.getName());

    private final YamlMapping fieldMap;
    private final ActivityDatabases activities;
    private final Satellites satellites;
    private final QrzService qrzService;
    private final TransformControl control;
    private final AdifQrzEnricher enricher;
    private final FromLocationDeterminer fromLocationDeterminer;
    private final ToLocationDeterminer toLocationDeterminer;
    private final ActivityProcessor activityProcessor;
    private final GeocodingProvider geocodingProvider;
    private final CommentTokenizer tokenizer;
    private final LocationParsers locationParsers;

    public CommentParsingAdifRecordTransformer(YamlMapping config, ActivityDatabases activities, QrzService qrzService, TransformControl control) {
        fieldMap = config.asMapping();
        this.activities = activities;
        this.qrzService = qrzService;
        this.control = control;
        this.enricher = new AdifQrzEnricher(qrzService);
        this.satellites = new Satellites();
        this.fromLocationDeterminer = new FromLocationDeterminer(control, qrzService, activities);
        this.toLocationDeterminer = new ToLocationDeterminer(control, qrzService, activities);
        this.activityProcessor = new ActivityProcessor(control, qrzService, activities);
        this.geocodingProvider = new NominatimGeocodingProvider();
        this.tokenizer = new ColonTokenizer();
        this.locationParsers = new LocationParsers(activities);
    }

    private void issueWarnings(Adif3Record rec) {
        // Check to see if a /P or /M station has a location, if not issue a warning
        String callsign = rec.getCall().trim().toUpperCase();
        boolean portable = CallsignUtils.isNotFixed(callsign);
        if (portable && rec.getMyCoordinates() == null && rec.getGridsquare() == null) {
            logger.warning(String.format("Contact with non-fixed station %s at %s does not have a location defined", callsign, rec.getTimeOn()));
        }
    }

    @Override
    public void transform(Qsos qsos, Adif3Record rec, int index) {
        /* Add Adif3Record details to the Qsos meta structure */
        Qso qso = createQsoFromAdif3Record(qsos, rec, index);

        activityProcessor.processActivities(qso.getFrom(), rec);

        Map<String, String> unmapped = new HashMap<>();
        QrzCallsign myQrzData = fromLocationDeterminer.setMyLocation(qso);
        qso.getFrom().setQrzInfo(myQrzData);
        enricher.enrichAdifForMe(qso.getRecord(), myQrzData);

        /* Load QRZ.COM info for the worked station as a fixed station, for information */
        QrzCallsign theirQrzData = qrzService.getCallsignData(qso.getTo().getCallsign());
        qso.getTo().setQrzInfo(theirQrzData);
        enricher.enrichAdifForThem(qso.getRecord(), theirQrzData);

        if (rec.getSotaRef() != null && StringUtils.isNotBlank(rec.getSotaRef().getValue())) {
            String sotaId = rec.getSotaRef().getValue();
            toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.SOTA, sotaId, unmapped);
            qso.getTo().addActivity(activities.getDatabase(ActivityType.SOTA).get(sotaId));
        }

        // Check the callsign for a Railways on the Air
        Activity rotaInfo = activities.getDatabase(ActivityType.ROTA).get(rec.getCall().toUpperCase());
        if (rotaInfo != null) {
            toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.ROTA, rotaInfo.getRef(), unmapped);
            qso.getTo().addActivity(rotaInfo);
        }

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

        if (StringUtils.isNotBlank(rec.getComment())) {
            transformComment(qso, rec.getComment(), unmapped);
        }

        if (rec.getCoordinates() == null && StringUtils.isBlank(rec.getGridsquare())) {
            theirQrzData = enricher.lookupLocationFromQrz(rec);
            qso.getTo().setQrzInfo(theirQrzData);
        }

        // IF qrz.com can't fill in the coordinates, and the gridsquare is set, fill in coordinates from that
        if (rec.getCoordinates() == null && MaidenheadLocatorConversion.isAValidGridSquare(rec.getGridsquare()) && !MaidenheadLocatorConversion.isADubiousGridSquare(rec.getGridsquare())) {
            // Set Coordinates from GridSquare that has been supplied in the input file
            rec.setCoordinates(MaidenheadLocatorConversion.locatorToCoords(rec.getGridsquare()));
        }

        // Last resort, attempt to find location from qrz.com address data via geolocation provider
        if (rec.getCoordinates() == null &&
                (!MaidenheadLocatorConversion.isAValidGridSquare(rec.getGridsquare()) ||
                MaidenheadLocatorConversion.isADubiousGridSquare(rec.getGridsquare())) &&
                theirQrzData != null) {
            try {
                rec.setCoordinates(geocodingProvider.getLocationFromAddress(theirQrzData));
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

        // Look to see if there is anything in the SIG/SIGINFO fields
        if (StringUtils.isNotBlank(rec.getSig())) {
            processSig(qso, unmapped);
        }

        if (!unmapped.isEmpty()) {
            addUnmappedToRecord(rec, unmapped);
        } else {
            // done a good job and slotted all the key/value pairs in the right place
            rec.setComment("");
        }

        // Add the SOTA Microwave Award data to the end of the comment field
        if (control.getSotaMicrowaveAwardComment() != null && control.getSotaMicrowaveAwardComment()) {
            SotaMicrowaveAward.addSotaMicrowaveAwardToComment(rec);
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
                    if (rec.getSotaRef() == null || StringUtils.isBlank(rec.getSotaRef().getValue())) {
                        toLocationDeterminer.setTheirLocationFromActivity(qso, activity);
                    }
                    unmapped.put(activityType, activityLocation);
                }
            }
        }
    }

    private Qso createQsoFromAdif3Record(Qsos qsos, Adif3Record rec, int index) {
        Qso qso = new Qso();
        qso.setIndex(index);
        qso.setRecord(rec);
        qsos.addQso(qso);
        return qso;
    }

    /**
     * Parse the Fast Log Entry comment string for pairs of key and values, for example
     * OP: John, QTH: Gatwick, PWR: 100W, ANT: Inv-V, WX: 4 degC, GRID: IO84io
     * In this case OP, QTH and PWR are transferred into their respective ADIF records,
     * and ANT/WX records are appended to the comment
     */
    private void transformComment(Qso qso, String comment, Map<String, String> unmapped) {
        Adif3Record rec = qso.getRecord();
        // try and split the comment up into comma separated list
        Map<String, String> tokens = tokenizer.tokenize(comment);

        // If this is a 'regular comment' then don't tokenize
        if (StringUtils.isNotEmpty(comment) && tokens.size() == 0) {
            unmapped.put(comment, "");
            return;
        }

        Double latitude = null;
        Double longitude = null;
        GlobalCoordinates coords = null;
        for (String key : tokens.keySet()) {
            String value = tokens.get(key).trim();

            YamlNode keyNode = fieldMap.value(key);
            if (keyNode != null) {
                YamlNode adifFieldYn = keyNode.asScalar();
                String adifField = adifFieldYn.asScalar().value();

                switch (adifField) {
                    case "Name":
                        rec.setName(value);
                        break;
                    case "Qth":
                        rec.setQth(value);
                        break;
                    case "Rig":
                        rec.setRig(value);
                        break;
                    case "Age":
                        rec.setAge(Integer.parseInt(value));
                        break;
                    case "Iota":
                        try {
                            Iota iota = Iota.findByCode(value);
                            rec.setIota(iota);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            logger.severe(String.format("Couldn't parse IOTA field %s for call %s at %s, please check, leaving it unmapped", value, rec.getCall(), rec.getTimeOn()));
                            unmapped.put(key, value);
                        }
                        break;
                    case "GridSquare":
                        if (MaidenheadLocatorConversion.isAValidGridSquare(value)) {
                            switch (value.length()) {
                                case 4:
                                case 6:
                                case 8:
                                case 10:
                                    rec.setGridsquare(value);
                                    rec.setCoordinates(MaidenheadLocatorConversion.locatorToCoords(value));
                                    break;
                                default:
                                    logger.severe(String.format("Gridsquare %s isn't valid", value));
                                    break;
                            }
                        } else {
                            logger.severe(String.format("Gridsquare %s isn't valid", value));
                        }
                        break;
                    case "RxPwr":
                        try {
                            rec.setRxPwr(parsePwr(value));
                        } catch (NumberFormatException nfe) {
                            logger.warning(String.format("Couldn't parse RxPwr field: %s, leaving it unmapped", value));
                            unmapped.put(key, value);
                        }
                        break;
                    case "BandRx":
                        try {
                            Band band = Band.findByCode(value);
                            rec.setBandRx(band);
                        } catch (ArrayIndexOutOfBoundsException e) {
                            logger.severe(String.format("Couldn't parse BandRx field %s for call %s at %s, please check, leaving it unmapped", value, rec.getCall(), rec.getTimeOn()));
                            unmapped.put(key, value);
                        }
                        break;
                    case "FrequencyRx":
                        try {
                            rec.setFreqRx(Double.parseDouble(value));
                        } catch (NumberFormatException nfe) {
                            logger.severe(String.format("Couldn't parse FrequencyRx field %s for call %s at %s, please check, leaving it unmapped", value, rec.getCall(), rec.getTimeOn()));
                            unmapped.put(key, value);
                        }
                        break;
                    case "TxPwr":
                        try {
                            rec.setTxPwr(parsePwr(value));
                        } catch (NumberFormatException nfe) {
                            logger.warning(String.format("Couldn't parse TxPwr field: %s, leaving it unmapped", value));
                            unmapped.put(key, value);
                        }
                        break;
                    case "SotaRef":
                        // Strip off any S2s reference
                        String sotaRef = StringUtils.split(value, ' ')[0];
                        try {
                            Sota sota = Sota.valueOf(sotaRef.toUpperCase());
                            rec.setSotaRef(sota);
                            toLocationDeterminer.setTheirCoordFromSotaId(qso, sotaRef, unmapped);
                            qso.getTo().addActivity(activities.getDatabase(ActivityType.SOTA).get(sotaRef));
                        } catch (IllegalArgumentException iae) {
                            // something we can't work out about the reference, so put it in the unmapped list instead
                            logger.severe(String.format("Couldn't identify %s as a SOTA reference in field %s, leaving it unmapped", sotaRef, value));
                            unmapped.put(key, value);
                        }
                        // We also add the Sota reference as-is to the comment field
                        unmapped.put(key, value);
                        break;
                    case "WotaRef":
                        // Strip off any S2s reference
                        String wotaId = StringUtils.split(value, ' ')[0];
                        toLocationDeterminer.setTheirCoordFromWotaId(qso, wotaId.toUpperCase(), unmapped);
                        qso.getTo().addActivity(activities.getDatabase(ActivityType.WOTA).get(wotaId));
                        break;
                    case "HemaRef":
                        // Strip off any S2s reference
                        String hemaId = StringUtils.split(value, ' ')[0];
                        toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.HEMA, hemaId.toUpperCase(), unmapped);
                        qso.getTo().addActivity(activities.getDatabase(ActivityType.HEMA).get(hemaId));
                        break;
                    case "PotaRef":
                        // Strip off any S2s reference
                        String potaId = StringUtils.split(value, ' ')[0];
                        toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.POTA, potaId.toUpperCase(), unmapped);
                        qso.getTo().addActivity(activities.getDatabase(ActivityType.POTA).get(potaId));
                        break;
                    case "CotaRef":
                        // Strip off any S2s reference
                        String cotaId = StringUtils.split(value, ' ')[0];
                        toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.COTA, cotaId.toUpperCase(), unmapped);
                        qso.getTo().addActivity(activities.getDatabase(ActivityType.COTA).get(cotaId));
                        break;
                    case "WwffRef":
                        // Strip off any S2s reference
                        String wwffId = StringUtils.split(value, ' ')[0];
                        toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.WWFF, wwffId.toUpperCase(), unmapped);
                        qso.getTo().addActivity(activities.getDatabase(ActivityType.WWFF).get(wwffId));
                        break;
                    case "SerialTx":
                        // Determine if this is a serial number of string based contest exchange
                        try {
                            rec.setStx(Integer.parseInt(value));
                        } catch (NumberFormatException nfe) {
                            // Not a simple number, so use the string ADIF field instead
                            rec.setStxString(value);
                        }
                        break;
                    case "SerialRx":
                        // Determine if this is a serial number of string based contest exchange
                        try {
                            rec.setSrx(Integer.parseInt(value));
                        } catch (NumberFormatException nfe) {
                            // Not a simple number, so use the string ADIF field instead
                            rec.setSrxString(value);
                        }
                        break;
                    case "Fists":
                        rec.setFists(value);
                        break;
                    case "Skcc":
                        rec.setSkcc(value);
                        break;
                    case "Qsl":
                        rec.setQslSDate(LocalDate.now());
                        rec.setQslSent(QslSent.SENT);
                        // This could either be a bureau or direct QSL depending on value
                        switch (value) {
                            case "D":
                                rec.setQslSentVia(QslVia.DIRECT);
                                break;
                            case "B":
                                rec.setQslSentVia(QslVia.BUREAU);
                                break;
                        }
                        break;
                    case "Coordinates":
                        coords = locationParsers.parseStringForCoordinates(value);
                        break;
                    case "Latitude":
                        try {
                            latitude = Double.parseDouble(value);
                        } catch (NumberFormatException e) {
                            logger.severe(String.format("Callsign: %s at %s has invalid latitude: %s", rec.getCall(), rec.getTimeOn().toString(), value));
                        }
                        break;
                    case "Longitude":
                        try {
                            longitude = Double.parseDouble(value);
                        } catch (NumberFormatException e) {
                            logger.severe(String.format("Callsign: %s at %s has invalid longitude: %s", rec.getCall(), rec.getTimeOn().toString(), value));
                        }
                        break;
                    case "AntPath":
                        AntPath path = AntPath.SHORT;
                        try {
                            path = AntPath.findByCode(value.toUpperCase());
                        } catch (Exception e) {
                            logger.severe(String.format("AntPath: %s isn't one of 'G': GRAYLINE, 'S': SHORT, 'L': LONG, 'O': OTHER", value));
                        }
                        rec.setAntPath(path);
                        break;
                    case "Propagation":
                        Propagation mode = Propagation.IONOSCATTER;
                        try {
                            mode = Propagation.findByCode(value.toUpperCase());
                        } catch (Exception e) {
                            logger.severe(String.format("Propagation: %s isn't one of the supported values for ADIF field PROP_MODE", value));
                        }
                        rec.setPropMode(mode);
                        break;
                    case "SatelliteName":
                        if (satellites.getSatellite(value.toUpperCase()) != null) {
                            rec.setSatName(value.toUpperCase());
                        } else {
                            logger.warning(String.format("Satellite: %s isn't currently supported", value));
                        }
                        break;
                    case "SatelliteMode":
                        rec.setSatMode(value);
                        break;
                    case "Notes":
                        rec.setNotes(value);
                        break;
                }
            } else {
                unmapped.put(key, value);
            }
            issueWarnings(rec);
        }
        if (coords != null || (latitude != null && longitude != null)) {
            if (coords == null) {
                coords = new GlobalCoordinates(latitude, longitude);
            }
            rec.setCoordinates(coords);
            rec.setGridsquare(MaidenheadLocatorConversion.coordsToLocator(coords));
            logger.info(String.format("Override location of %s: %s", rec.getCall(), rec.getCoordinates().toString()));
        }
    }

    /**
     * Process a power string into a double
     */
    private double parsePwr(String value) throws NumberFormatException {
        String pwr = value.toLowerCase().trim();
        if (pwr.endsWith("w")) {
            pwr = StringUtils.replace(pwr, "w", "");
        } else if (pwr.endsWith(" w")) {
            pwr = StringUtils.replace(pwr, " w", "");
        } else if (pwr.endsWith(" watt")) {
            pwr = StringUtils.replace(pwr, " watt", "");
        } else if (pwr.endsWith(" watts")) {
            pwr = StringUtils.replace(pwr, " watts", "");
        }
        if (pwr.endsWith("k")) {
            pwr = StringUtils.replace(pwr, "k", "000");
        }
        return Double.parseDouble(pwr);
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
