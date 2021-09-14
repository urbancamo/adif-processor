package uk.m0nom.adif3.transform;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.AntPath;
import org.marsik.ham.adif.enums.Propagation;
import org.marsik.ham.adif.enums.QslSent;
import org.marsik.ham.adif.enums.QslVia;
import org.marsik.ham.adif.types.Iota;
import org.marsik.ham.adif.types.Sota;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.wota.WotaSummitInfo;
import uk.m0nom.activity.wota.WotaSummitsDatabase;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.adif3.contacts.Station;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.qrz.QrzCallsign;
import uk.m0nom.qrz.QrzXmlService;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

public class CommentParsingAdifRecordTransformer implements Adif3RecordTransformer {
    private static final Logger logger = Logger.getLogger(CommentParsingAdifRecordTransformer.class.getName());

    private final YamlMapping fieldMap;
    private final ActivityDatabases activities;
    private final QrzXmlService qrzXmlService;
    private final TransformControl control;
    private final AdifQrzEnricher enricher;
    private boolean reportedLocationOverride = false;

    private final String[] portableSuffixes = new String[] {"/P", "/M", "/MM", "/PM"};

    public CommentParsingAdifRecordTransformer(YamlMapping config, ActivityDatabases activities, QrzXmlService qrzXmlService, TransformControl control) {
        fieldMap = config.asMapping();
        this.activities = activities;
        this.qrzXmlService = qrzXmlService;
        this.control = control;
        this.enricher = new AdifQrzEnricher();
    }

    private void setTheirCoordFromActivity(Adif3Record rec, ActivityType activity, String reference, Map<String, String> unmapped) {
        Activity info = activities.getDatabase(activity).get(reference);
        if (info != null) {
            if (info.hasCoords()) {
                rec.setCoordinates(info.getCoords());
                rec.setGridsquare(MaidenheadLocatorConversion.coordsToLocator(info.getCoords()));
            } else if (info.hasGrid()) {
                GlobalCoordinates coords = MaidenheadLocatorConversion.locatorToCoords(info.getGrid());
                rec.setMyCoordinates(coords);
                rec.setGridsquare(info.getGrid());
            }
            // If the SIG isn't set, add it here
            if (StringUtils.isEmpty(rec.getSig())) {
                rec.setSig(activity.getActivityName());
                rec.setSigInfo(reference);
            }
            // Add the activity to the unmapped list
            unmapped.put(activity.getActivityName(), reference);
        } else {
            logger.warning(String.format("Suspicious %s reference %s for callsign %s at %s", activity.getActivityName(), reference, rec.getCall(), rec.getTimeOn().toString()));
        }
    }

    private void setTheirCoordFromSotaId(Adif3Record rec, String sotaId, Map<String, String> unmapped) {
        setTheirCoordFromActivity(rec, ActivityType.SOTA, sotaId, unmapped);
        Activity sotaInfo = activities.getDatabase(ActivityType.SOTA).get(sotaId);
        if (sotaInfo != null) {
            // See if this is also a WOTA
            WotaSummitsDatabase wotaSummitsDatabase = (WotaSummitsDatabase) activities.getDatabase(ActivityType.WOTA);
            Activity wotaInfo = wotaSummitsDatabase.getFromSotaId(sotaId);
            if (wotaInfo != null) {
                unmapped.put("WOTA", wotaInfo.getRef());
            }
        }
    }

    private void setTheirCoordFromWotaId(Adif3Record rec, String wotaId, Map<String, String> unmapped) {
        setTheirCoordFromActivity(rec, ActivityType.WOTA, wotaId, unmapped);
        WotaSummitInfo wotaInfo = (WotaSummitInfo) activities.getDatabase(ActivityType.WOTA).get(wotaId);
        if (wotaInfo != null) {
            String sotaId = wotaInfo.getSotaId();
            if (sotaId != null) {
                // SOTA Latitude/Longitude is more accurate, so overwrite from that information
                setTheirCoordFromSotaId(rec, sotaId, unmapped);
            } else {
                unmapped.put("WOTA", wotaInfo.getRef());
            }
        }
    }

    private void setMyLocationFromWotaId(Adif3Record rec, String wotaId) {
        WotaSummitInfo wotaInfo = (WotaSummitInfo) activities.getDatabase(ActivityType.WOTA).get(wotaId);
        if (wotaInfo != null) {
            rec.setMyCoordinates(wotaInfo.getCoords());

            // Also set the GridSquare as a fallback
            rec.setMyGridSquare(MaidenheadLocatorConversion.coordsToLocator(wotaInfo.getCoords()));

            String sotaId = wotaInfo.getSotaId();
            if (sotaId != null) {
                // SOTA Latitude/Longitude is more accurate, so overwrite from that information
                setMyLocationFromActivity(rec, ActivityType.SOTA, sotaId);
            }
        } else {
            logger.warning(String.format("Suspicious WOTA reference %s for callsign: %s", wotaId, rec.getStationCallsign()));
        }
    }

    private void setMyLocationFromHemaId(Adif3Record rec, String hemaId) {
        Activity hemaInfo = activities.getDatabase(ActivityType.HEMA).get(hemaId);
        if (hemaInfo != null) {
            rec.setMyCoordinates(hemaInfo.getCoords());

            // Also set the GridSquare as a fallback
            rec.setMyGridSquare(MaidenheadLocatorConversion.coordsToLocator(hemaInfo.getCoords()));
        } else {
            logger.warning(String.format("Suspicious HEMA reference %s for your callsign %s", hemaId, rec.getStationCallsign()));
        }
    }

    private void setMyLocationFromActivity(Adif3Record rec, ActivityType activity, String ref) {
        Activity info = activities.getDatabase(activity).get(ref);
        if (info != null) {
            if (rec.getMyCoordinates() == null) {
                if (info.hasCoords()) {
                    rec.setMyCoordinates(info.getCoords());
                    rec.setMyGridSquare(MaidenheadLocatorConversion.coordsToLocator(info.getCoords()));
                } else if (info.hasGrid()) {
                    rec.setMyGridSquare(info.getGrid());
                    rec.setMyCoordinates(MaidenheadLocatorConversion.locatorToCoords(info.getGrid()));
                }
            }
        } else {
            logger.warning(String.format("Suspicious %s reference %s for callsign %s at %s", activity.getActivityName(), ref, rec.getCall(), rec.getTimeOn().toString()));
        }
    }

    private void setMyLocationFromGrid(Qso qso, String myGrid) {
        Adif3Record rec = qso.getRecord();
        qso.getRecord().setMyGridSquare(myGrid.substring(4));
        qso.getFrom().setGrid(myGrid);
        rec.setMyCoordinates(MaidenheadLocatorConversion.locatorToCoords(myGrid));
        qso.getFrom().setCoordinates(rec.getMyCoordinates());
    }

    private void setHemaOrSotaFromWota(Station station, String wotaId) {
        WotaSummitInfo wotaInfo = (WotaSummitInfo) activities.getDatabase(ActivityType.WOTA).get(wotaId);

        station.addActivity(activities.getDatabase(ActivityType.HEMA).get(wotaInfo.getHemaId()));
        station.addActivity(activities.getDatabase(ActivityType.SOTA).get(wotaInfo.getSotaId()));
    }

    private void setWotaFromHemaId(Station station, String hemaId) {
        WotaSummitsDatabase wotaDatabase = (WotaSummitsDatabase) activities.getDatabase(ActivityType.WOTA);
        station.addActivity(wotaDatabase.getFromHemaId(hemaId));
    }

    private void setWotaFromSotaId(Station station, String sotaId) {
        WotaSummitsDatabase wotaDatabase = (WotaSummitsDatabase) activities.getDatabase(ActivityType.WOTA);
        station.addActivity(wotaDatabase.getFromSotaId(sotaId));
    }

    private QrzCallsign setMyLocation(Qso qso) {
        Adif3Record rec = qso.getRecord();
        // Attempt a lookup from QRZ.com
        QrzCallsign callsignData = qrzXmlService.getCallsignData(rec.getStationCallsign());
        boolean locationOverride = false;

        if (StringUtils.isNotEmpty(control.getWota())) {
            qso.getFrom().addActivity(activities.getDatabase(ActivityType.WOTA).get(control.getWota().toUpperCase()));
            setHemaOrSotaFromWota(qso.getFrom(), control.getWota().toUpperCase());
        }
        if (StringUtils.isNotEmpty(control.getSota())) {
            qso.getFrom().addActivity(activities.getDatabase(ActivityType.SOTA).get(control.getSota()));
            setWotaFromSotaId(qso.getFrom(), control.getSota().toUpperCase());
        } else if (rec.getMySotaRef() != null) {
            String sotaRef = rec.getMySotaRef().getValue().toUpperCase();
            qso.getFrom().addActivity(activities.getDatabase(ActivityType.SOTA).get(sotaRef));
            setWotaFromSotaId(qso.getFrom(), sotaRef);
        }
        if (StringUtils.isNotEmpty(control.getHema())) {
            qso.getFrom().addActivity(activities.getDatabase(ActivityType.HEMA).get(control.getHema().toUpperCase()));
            setWotaFromHemaId(qso.getFrom(), control.getHema().toUpperCase());
        }
        if (StringUtils.isNotEmpty(control.getPota())) {
            qso.getFrom().addActivity(activities.getDatabase(ActivityType.POTA).get(control.getPota().toUpperCase()));
        }
        if (StringUtils.isNotEmpty(control.getWwff())) {
            qso.getFrom().addActivity(activities.getDatabase(ActivityType.WWFF).get(control.getWwff().toUpperCase()));
        }

        if (StringUtils.isNotEmpty(control.getMyLatitude()) && StringUtils.isNotEmpty(control.getMyLongitude())) {
            double latitude = Double.parseDouble(StringUtils.remove(control.getMyLatitude(), '\''));
            double longitude = Double.parseDouble(StringUtils.remove(control.getMyLongitude(), '\''));
            rec.setMyCoordinates(new GlobalCoordinates(latitude, longitude));
            locationOverride = true;
            reportLocationOverride(rec.getStationCallsign(), latitude, longitude);
        }

        // Check to see whether a Maidenhead locator has been specified in the control
        // structure. This overrides my location at the end, unless coordinates have also been specified.
        if (control.getMyGrid() != null && (control.getMyLatitude() == null || control.getMyLongitude() == null)
                && MaidenheadLocatorConversion.isAValidGridSquare(control.getMyGrid())) {
            setMyLocationFromGrid(qso, control.getMyGrid());
            reportLocationOverride(rec.getStationCallsign(), control.getMyGrid());
        }

        if (rec.getMyCoordinates() == null) {
            if (StringUtils.isNotEmpty(control.getSota())) {
                if (!locationOverride) {
                    setMyLocationFromActivity(rec, ActivityType.SOTA, control.getSota().toUpperCase());
                }
            } else if (StringUtils.isNotEmpty(control.getWota())) {
                if (!locationOverride) {
                    setMyLocationFromWotaId(rec, control.getWota().toUpperCase());
                }
            } else if (StringUtils.isNotEmpty(control.getHema())) {
                if (!locationOverride) {
                    setMyLocationFromHemaId(rec, control.getHema().toUpperCase());
                }
            } else if (rec.getMySotaRef() != null) {
                if (!locationOverride) {
                    setMyLocationFromActivity(rec, ActivityType.SOTA, rec.getMySotaRef().getValue());
                }
                setWotaFromSotaId(qso.getFrom(), rec.getMySotaRef().getValue());
            } else if (StringUtils.isNotEmpty(control.getPota())) {
                if (!locationOverride) {
                    setMyLocationFromActivity(rec, ActivityType.POTA, control.getPota().toUpperCase());
                }
            } else if (StringUtils.isNotEmpty(control.getWwff())) {
                if (!locationOverride) {
                    setMyLocationFromActivity(rec, ActivityType.WWFF, control.getWwff().toUpperCase());
                }
            } else if (StringUtils.isNotEmpty(control.getMyGrid())) {
                /* If user has supplied a maidenhead location, use that in preference */
                if (MaidenheadLocatorConversion.isAValidGridSquare(control.getMyGrid())) {
                    rec.setMyGridSquare(control.getMyGrid());
                }

                if (rec.getMyGridSquare() == null) {
                    if (callsignData != null) {
                        if (callsignData.getLat() != null && callsignData.getLon() != null) {
                            GlobalCoordinates coord = new GlobalCoordinates(callsignData.getLat(), callsignData.getLon());
                            rec.setMyCoordinates(coord);
                        } else if (callsignData.getGrid() != null) {
                            if (MaidenheadLocatorConversion.isAValidGridSquare(callsignData.getGrid())) {
                                rec.setMyGridSquare(callsignData.getGrid());
                            }
                        }
                    }
                }
                if (rec.getMyGridSquare() != null && MaidenheadLocatorConversion.isAValidGridSquare(rec.getMyGridSquare())) {
                    // Less Accurate from a Gridsquare, but better than nothing
                    GlobalCoordinates myLoc = MaidenheadLocatorConversion.locatorToCoords(rec.getMyGridSquare());
                    rec.setMyCoordinates(myLoc);
                }
            } else if (callsignData != null && callsignData.getLat() != null && callsignData.getLon() != null) {
                GlobalCoordinates coord = new GlobalCoordinates(callsignData.getLat(), callsignData.getLon());
                rec.setMyCoordinates(coord);
                qso.getFrom().setCoordinates(coord);
            } else if (callsignData != null && callsignData.getGrid() != null) {
                rec.setMyGridSquare(callsignData.getGrid());
                setMyLocationFromGrid(qso, callsignData.getGrid());
            }
        }
        return callsignData;
    }

    private void reportLocationOverride(String stationCallsign, String grid) {
        if (!reportedLocationOverride) {
            logger.info(String.format("Overriding location of %s to grid: %s",
                    stationCallsign, grid));
            reportedLocationOverride = true;
        }
    }

    private void reportLocationOverride(String stationCallsign, double latitude, double longitude) {
        if (!reportedLocationOverride) {
            logger.info(String.format("Overriding location of %s to lat: %.3f, long: %.3f",
                    stationCallsign, latitude, longitude));
            reportedLocationOverride = true;
        }
    }

    private boolean isPortable(String callsign) {
        return StringUtils.endsWithAny(callsign, portableSuffixes);
    }

    private void issueWarnings(Adif3Record rec) {
        // Check to see if a /P or /M station has a location, if not issue a warning
        String callsign = rec.getCall().trim().toUpperCase();
        boolean portable = isPortable(callsign);
        if (portable && rec.getMyCoordinates() == null && rec.getGridsquare() == null) {
            logger.warning(String.format("Contact with non-fixed station %s at %s does not have a location defined", callsign, rec.getTimeOn()));
        }
    }

    private QrzCallsign lookupLocationFromQrz(Adif3Record rec) {
        String callsign = rec.getCall();
        QrzCallsign callsignData = qrzXmlService.getCallsignData(callsign);
        if (callsignData != null) {
           updateRecordFromQrzLocation(rec, callsignData);
            logger.info(String.format("Retrieved %s from QRZ.COM", callsign));
        } else if (isPortable(callsign)) {
            // Try stripping off any portable callsign information and querying that as a last resort
            String fixedCallsign = callsign.substring(0, StringUtils.lastIndexOf(callsign, "/"));
            callsignData = qrzXmlService.getCallsignData(fixedCallsign);
            if (callsignData != null) {
                logger.info(String.format("Retrieved %s from QRZ.COM using FIXED station callsign %s", callsign, fixedCallsign));
                updateRecordFromQrzLocation(rec, callsignData);
            }
       }
        return callsignData;
    }

    /**
     * Attempt to update the location based on callsign data downloaded from QRZ.COM
     * What we need to be careful of here is of bad data. For example, some users set geoloc to
     * grid but then the grid isn't valid. We need to ignore that, or we'll set the station to
     * be antartica.
     */
    private void updateRecordFromQrzLocation(Adif3Record rec, QrzCallsign callsignData) {
        if (callsignData != null) {
            if (rec.getCoordinates() == null) {
                boolean gridBasedGeoloc = StringUtils.equalsIgnoreCase("grid", callsignData.getGeoloc());
                String gridSquare = callsignData.getGrid();
                boolean invalidGridBasedLoc = gridBasedGeoloc && !MaidenheadLocatorConversion.isAValidGridSquare(gridSquare);

                if (callsignData.getLat() != null && callsignData.getLon() != null && !invalidGridBasedLoc) {
                    GlobalCoordinates coord = new GlobalCoordinates(callsignData.getLat(), callsignData.getLon());
                    rec.setCoordinates(coord);
                }
                if (rec.getGridsquare() == null && !invalidGridBasedLoc) {
                    rec.setGridsquare(callsignData.getGrid());
                }
            }
        }
    }

    @Override
    public void transform(Qsos qsos, Adif3Record rec, int index) {
        /* Add Adif3Record details to the Qsos meta structure */
        Qso qso = createQsoFromAdif3Record(qsos, rec, index);

        Map<String, String> unmapped = new HashMap<>();
        QrzCallsign myQrzData = setMyLocation(qso);
        qso.getFrom().setQrzInfo(myQrzData);
        enricher.enrichAdifForMe(qso.getRecord(), myQrzData);

        /* Load QRZ.COM info for the worked station as a fixed station, for information */
        QrzCallsign theirQrzData = qrzXmlService.getCallsignData(qso.getTo().getCallsign());
        qso.getTo().setQrzInfo(theirQrzData);
        enricher.enrichAdifForThem(qso.getRecord(), theirQrzData);

        // Duplicate references into the comment
        if (rec.getSotaRef() != null && StringUtils.isNotBlank(rec.getSotaRef().getValue())) {
            String sotaId = rec.getSotaRef().getValue();
            setTheirCoordFromActivity(rec, ActivityType.SOTA, sotaId, unmapped);
            qso.getTo().addActivity(activities.getDatabase(ActivityType.SOTA).get(sotaId));
        }

        if (StringUtils.isNotBlank(rec.getComment())) {
            transformComment(qso, rec.getComment(), unmapped);
        }

        if (rec.getCoordinates() == null && StringUtils.isBlank(rec.getGridsquare())) {
            theirQrzData = lookupLocationFromQrz(rec);
            qso.getTo().setQrzInfo(theirQrzData);
        }
        // IF qrz.com can't fill in the coordinates, and the gridsquare is set, fill in coordinates from that
        if (rec.getCoordinates() == null && StringUtils.isNotBlank(rec.getGridsquare())) {
            // Set Coordinates from GridSquare that has been supplied in the input file
            rec.setCoordinates(MaidenheadLocatorConversion.locatorToCoords(rec.getGridsquare()));
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
                        setTheirLocationFromActivity(qso, activity);
                    }
                    unmapped.put(activityType, activityLocation);
                }
            }
        }
    }

    private void setTheirLocationFromActivity(Qso qso, Activity activity) {
        if (activity.hasCoords()) {
            GlobalCoordinates coords = activity.getCoords();
            String grid = MaidenheadLocatorConversion.coordsToLocator(coords);

            qso.getTo().setCoordinates(coords);
            qso.getRecord().setCoordinates(coords);
            qso.getTo().setGrid(grid);
            qso.getRecord().setGridsquare(grid);
        } else if (activity.hasGrid()) {
            String grid = activity.getGrid();
            qso.getTo().setGrid(grid);
            qso.getRecord().setGridsquare(grid);
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
        Map<String, String> tokens = tokenize(comment);

        // If this is a 'regular comment' then don't tokenize
        if (StringUtils.isNotEmpty(comment) && tokens.size() == 0) {
            unmapped.put(comment, "");
            return;
        }

        Double latitude = null;
        Double longitude = null;

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
                                    rec.setGridsquare(value.substring(0, value.length()));
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
                        String pwr = value.toLowerCase(Locale.ROOT).trim();
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
                        try {
                            rec.setRxPwr(Double.parseDouble(pwr));
                        } catch (NumberFormatException nfe) {
                            logger.warning(String.format("Couldn't parse RxPwr field: %s, leaving it unmapped", value));
                            unmapped.put(key, value);
                        }
                        break;
                    case "SotaRef":
                        // Strip off any S2s reference
                        String sotaRef = StringUtils.split(value, ' ')[0];
                        try {
                            Sota sota = Sota.valueOf(sotaRef.toUpperCase());
                            rec.setSotaRef(sota);
                            setTheirCoordFromSotaId(rec, sotaRef, unmapped);
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
                        setTheirCoordFromWotaId(rec, wotaId.toUpperCase(), unmapped);
                        qso.getTo().addActivity(activities.getDatabase(ActivityType.WOTA).get(wotaId));
                        break;
                    case "HemaRef":
                        // Strip off any S2s reference
                        String hemaId = StringUtils.split(value, ' ')[0];
                        setTheirCoordFromActivity(rec, ActivityType.HEMA, hemaId.toUpperCase(), unmapped);
                        qso.getTo().addActivity(activities.getDatabase(ActivityType.HEMA).get(hemaId));
                        break;
                    case "PotaRef":
                        // Strip off any S2s reference
                        String potaId = StringUtils.split(value, ' ')[0];
                        setTheirCoordFromActivity(rec, ActivityType.POTA, potaId.toUpperCase(), unmapped);
                        qso.getTo().addActivity(activities.getDatabase(ActivityType.POTA).get(potaId));
                        break;
                    case "CotaRef":
                        // Strip off any S2s reference
                        String cotaId = StringUtils.split(value, ' ')[0];
                        setTheirCoordFromActivity(rec, ActivityType.COTA, cotaId.toUpperCase(), unmapped);
                        qso.getTo().addActivity(activities.getDatabase(ActivityType.COTA).get(cotaId));
                        break;
                    case "WwffRef":
                        // Strip off any S2s reference
                        String wwffId = StringUtils.split(value, ' ')[0];
                        setTheirCoordFromActivity(rec, ActivityType.WWFF, wwffId.toUpperCase(), unmapped);
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
                            path = AntPath.valueOf(value);
                        } catch (Exception e) {
                            logger.severe(String.format("AntPath: %s isn't one of 'G': GRAYLINE, 'S': SHORT, 'L': LONG, 'O': OTHER", value));
                        }
                        rec.setAntPath(path);
                        break;
                    case "Propagation":
                        Propagation mode = Propagation.IONOSCATTER;
                        try {
                            mode = Propagation.valueOf(value);
                        } catch (Exception e) {
                            logger.severe(String.format("Propagation: %s isn't one of the supported values for ADIF field PROP_MODE", value));
                        }
                        rec.setPropMode(mode);
                        break;
                }
            } else {
                unmapped.put(key, value);
            }
            issueWarnings(rec);
        }
        if (latitude != null && longitude != null) {
            GlobalCoordinates coords = new GlobalCoordinates(latitude, longitude);
            rec.setCoordinates(coords);
            rec.setGridsquare(MaidenheadLocatorConversion.coordsToLocator(coords));
            logger.info(String.format("Override location of %s: %s", rec.getCall(), rec.getCoordinates().toString()));
        }
    }

    /**
     * Any key/value pairs in the fast log entry comment string that can't be mapped into a specific ADIF field
     * are added to the comment string in the ADIF file
     * @param rec ADIF record
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


    private Map<String, String> tokenize(String comment) {
        Map<String, String> tokens = new HashMap<>();
        StringTokenizer tokenizer = new StringTokenizer(comment, ",");
        while (tokenizer.hasMoreTokens()) {
            String token = tokenizer.nextToken().trim();
            if (token.contains(":")) {
                String[] pair = StringUtils.split(token, ":");
                tokens.put(pair[0].trim(), pair[1].trim());
            }
        }
        return tokens;
    }
}
