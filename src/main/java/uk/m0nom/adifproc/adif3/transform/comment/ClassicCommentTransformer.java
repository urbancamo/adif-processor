package uk.m0nom.adifproc.adif3.transform.comment;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.*;
import org.marsik.ham.adif.types.Iota;
import org.marsik.ham.adif.types.Sota;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.adif3.config.TransformerConfig;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.transform.CallsignUtils;
import uk.m0nom.adifproc.adif3.transform.TransformResults;
import uk.m0nom.adifproc.adif3.transform.tokenizer.ColonTokenizer;
import uk.m0nom.adifproc.adif3.transform.tokenizer.CommentTokenizer;
import uk.m0nom.adifproc.coords.*;
import uk.m0nom.adifproc.location.ToLocationDeterminer;
import uk.m0nom.adifproc.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.adifproc.satellite.ApSatelliteService;

import java.time.LocalDate;
import java.util.Map;
import java.util.logging.Logger;

import static uk.m0nom.adifproc.adif3.transform.comment.parsers.FieldParseUtils.parsePwr;

/**
 * Parse the Fast Log Entry comment string for pairs of key and values, for example
 * OP: John, QTH: Gatwick, PWR: 100W, ANT: Inv-V, WX: 4 degC, GRID: IO84io
 * In this case OP, QTH and PWR are transferred into their respective ADIF records,
 * and ANT/WX records are appended to the comment
 */
@Service
public class ClassicCommentTransformer implements CommentTransformer {
    private static final Logger logger = Logger.getLogger(ClassicCommentTransformer.class.getName());
    private final CommentTokenizer tokenizer;
    private final YamlMapping fieldMap;
    private final ActivityDatabaseService activities;
    private final ToLocationDeterminer toLocationDeterminer;
    private final LocationParsingService locationParsingService;
    private final ApSatelliteService apSatelliteService;

    public ClassicCommentTransformer(TransformerConfig config,
                                     ActivityDatabaseService activities,
                                     ToLocationDeterminer toLocationDeterminer,
                                     ApSatelliteService apSatelliteService) {
        this.tokenizer = new ColonTokenizer();
        this.fieldMap = config.getConfig().asMapping();
        this.activities = activities;
        this.toLocationDeterminer = toLocationDeterminer;
        this.locationParsingService = new LocationParsingService();
        this.apSatelliteService = apSatelliteService;
    }

    @Override
    public void transformComment(Qso qso, String comment, Map<String, String> unmapped, TransformResults results) {
        if (StringUtils.isBlank(comment)) {
            return;
        }

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
        GlobalCoords3D coords = null;
        String callsignWithInvalidActivity = null;

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
                                    if (value.length() > 6) {
                                        // Truncate more accurate grid square values to 6 characters to put in the record
                                        // as it doesn't support any more accuracy than 6
                                        rec.setGridsquare(value.substring(0, 6));
                                    } else {
                                        rec.setGridsquare(value);
                                    }
                                    // Use full accuracy to set the coordinates
                                    GlobalCoords3D coordinates = MaidenheadLocatorConversion.locatorToCoords(LocationSource.OVERRIDE, value);
                                    rec.setCoordinates(coordinates);
                                    qso.getTo().setCoordinates(coordinates);
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
                            callsignWithInvalidActivity = toLocationDeterminer.setTheirLocationFromSotaId(qso, sotaRef);
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
                        callsignWithInvalidActivity = toLocationDeterminer.setTheirLocationFromWotaId(qso, wotaId.toUpperCase());
                        qso.getTo().addActivity(activities.getDatabase(ActivityType.WOTA).get(wotaId));
                        break;
                    case "GmaRef":
                        // Strip off any S2s reference
                        String gmaId = StringUtils.split(value, ' ')[0];
                        callsignWithInvalidActivity = toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.GMA, gmaId.toUpperCase());
                        qso.getTo().addActivity(activities.getDatabase(ActivityType.GMA).get(gmaId));
                        break;
                    case "HemaRef":
                        // Strip off any S2s reference
                        String hemaId = StringUtils.split(value, ' ')[0];
                        callsignWithInvalidActivity = toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.HEMA, hemaId.toUpperCase());
                        qso.getTo().addActivity(activities.getDatabase(ActivityType.HEMA).get(hemaId));
                        break;
                    case "PotaRef":
                        // Strip off any S2s reference
                        String potaId = StringUtils.split(value, ' ')[0];
                        callsignWithInvalidActivity = toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.POTA, potaId.toUpperCase());
                        qso.getTo().addActivity(activities.getDatabase(ActivityType.POTA).get(potaId));
                        break;
                    case "CotaRef":
                        // Strip off any S2s reference
                        String cotaId = StringUtils.split(value, ' ')[0];
                        callsignWithInvalidActivity = toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.COTA, cotaId.toUpperCase());
                        qso.getTo().addActivity(activities.getDatabase(ActivityType.COTA).get(cotaId));
                        break;
                    case "WwffRef":
                        // Strip off any S2s reference
                        String wwffId = StringUtils.split(value, ' ')[0];
                        callsignWithInvalidActivity = toLocationDeterminer.setTheirLocationFromActivity(qso, ActivityType.WWFF, wwffId.toUpperCase());
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
                        LocationParserResult parserResult = locationParsingService.parseStringForCoordinates(LocationSource.OVERRIDE, value);
                        if (parserResult != null) {
                            coords = parserResult.getCoords();
                        }
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
                        if (apSatelliteService.getSatellite(value, rec.getQsoDate())  != null) {
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
            if (callsignWithInvalidActivity != null) {
                results.addContactWithDubiousLocation(callsignWithInvalidActivity);
            }
            issueWarnings(rec);
        }
        if (coords != null || (latitude != null && longitude != null)) {
            if (coords == null) {
                coords = new GlobalCoords3D(latitude, longitude, LocationSource.OVERRIDE, LocationAccuracy.LAT_LONG);
            }
            qso.getTo().setCoordinates(coords);
            rec.setCoordinates(coords);
            rec.setGridsquare(MaidenheadLocatorConversion.coordsToLocator(coords));
            logger.info(String.format("Override location of %s: %s", rec.getCall(), rec.getCoordinates().toString()));
        }
    }

    private void issueWarnings(Adif3Record rec) {
        // Check to see if a /P or /M station has a location, if not issue a warning
        String callsign = rec.getCall().trim().toUpperCase();
        boolean portable = CallsignUtils.isPortable(callsign);
        if (portable && rec.getMyCoordinates() == null && rec.getGridsquare() == null) {
            logger.warning(String.format("Contact with non-fixed station %s at %s does not have a location defined", callsign, rec.getTimeOn()));
        }
    }
}
