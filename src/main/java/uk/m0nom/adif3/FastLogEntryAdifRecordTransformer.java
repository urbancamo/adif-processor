package uk.m0nom.adif3;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.QslSent;
import org.marsik.ham.adif.enums.QslVia;
import org.marsik.ham.adif.types.Iota;
import org.marsik.ham.adif.types.Sota;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.adif3.contacts.Station;
import uk.m0nom.hema.HemaSummitInfo;
import uk.m0nom.maidenheadlocator.LatLng;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.pota.PotaInfo;
import uk.m0nom.qrz.QrzCallsign;
import uk.m0nom.qrz.QrzXmlService;
import uk.m0nom.sota.SotaSummitInfo;
import uk.m0nom.summits.SummitsDatabase;
import uk.m0nom.wota.WotaSummitInfo;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

public class FastLogEntryAdifRecordTransformer implements Adif3RecordTransformer {
    private static final Logger logger = Logger.getLogger(FastLogEntryAdifRecordTransformer.class.getName());

    private final YamlMapping fieldMap;
    private final SummitsDatabase summits;
    private final QrzXmlService qrzXmlService;
    private final TransformControl control;
    private boolean reportedLocationOverride = false;

    private final String[] portableSuffixes = new String[] {"/P", "/M", "/MM", "/PM"};

    public FastLogEntryAdifRecordTransformer(YamlMapping config, SummitsDatabase summits, QrzXmlService qrzXmlService, TransformControl control) {
        fieldMap = config.asMapping();
        this.summits = summits;
        this.qrzXmlService = qrzXmlService;
        this.control = control;
    }

    private void setCoordFromSotaId(Adif3Record rec, String sotaId, Map<String, String> unmapped) {
        // Upload latitude and longitude based on SOTA reference
        SotaSummitInfo sotaInfo = summits.getSota().get(sotaId);
        if (sotaInfo != null) {
            GlobalCoordinates coord = new GlobalCoordinates(sotaInfo.getLatitude(), sotaInfo.getLongitude());
            rec.setCoordinates(coord);
            // Also set the GridSquare as a fallback
            rec.setGridsquare(MaidenheadLocatorConversion.latLngToLocator(sotaInfo.getLatitude(), sotaInfo.getLongitude()));
            // See if this is also a WOTA
            WotaSummitInfo wotaInfo = summits.getWota().getFromSotaId(sotaId);
            if (wotaInfo != null) {
                unmapped.put("WOTA", wotaInfo.getWotaId());
            }
        } else {
            logger.warning(String.format("Suspicious SOTA reference %s for callsign %s at %s", sotaId, rec.getCall(), rec.getTimeOn().toString()));
        }
    }

    private void setCoordFromWotaId(Adif3Record rec, String wotaId, Map<String, String> unmapped) {
        // Upload latitude and longitude based on SOTA reference
        WotaSummitInfo wotaInfo = summits.getWota().get(wotaId);
        if (wotaInfo != null) {
            GlobalCoordinates coord = new GlobalCoordinates(wotaInfo.getLatitude(), wotaInfo.getLongitude());
            rec.setCoordinates(coord);

            // Also set the GridSquare as a fallback
            rec.setGridsquare(MaidenheadLocatorConversion.latLngToLocator(wotaInfo.getLatitude(), wotaInfo.getLongitude()));

            String sotaId = wotaInfo.getSotaId();
            if (sotaId != null) {
                // SOTA Latitude/Longitude is more accurate, so overwrite from that information
                setCoordFromSotaId(rec, sotaId, unmapped);
            } else {
                unmapped.put("WOTA", wotaInfo.getWotaId());
            }
        } else {
            logger.warning(String.format("Suspicious WOTA reference %s for callsign %s at %s", wotaId, rec.getCall(), rec.getTimeOn().toString()));
        }
    }

    private void setMyLocationFromWotaId(Adif3Record rec, String wotaId) {
        // Upload latitude and longitude based on SOTA reference
        WotaSummitInfo wotaInfo = summits.getWota().get(wotaId);
        if (wotaInfo != null) {
            GlobalCoordinates coord = new GlobalCoordinates(wotaInfo.getLatitude(), wotaInfo.getLongitude());
            rec.setMyCoordinates(coord);

            // Also set the GridSquare as a fallback
            rec.setMyGridSquare(MaidenheadLocatorConversion.latLngToLocator(wotaInfo.getLatitude(), wotaInfo.getLongitude()));

            String sotaId = wotaInfo.getSotaId();
            if (sotaId != null) {
                // SOTA Latitude/Longitude is more accurate, so overwrite from that information
                setMyLocationFromSotaId(rec, sotaId);
            }
        } else {
            logger.warning(String.format("Suspicious WOTA reference %s for callsign: %s", wotaId, rec.getStationCallsign()));
        }
    }

    private void setCoordFromHemaId(Adif3Record rec, String hemaId, Map<String, String> unmapped) {
        // Upload latitude and longitude based on SOTA reference
        HemaSummitInfo hemaInfo = summits.getHema().get(hemaId);
        if (hemaInfo != null) {
            GlobalCoordinates coord = new GlobalCoordinates(hemaInfo.getLatitude(), hemaInfo.getLongitude());
            rec.setCoordinates(coord);

            // Also set the GridSquare as a fallback
            rec.setGridsquare(MaidenheadLocatorConversion.latLngToLocator(hemaInfo.getLatitude(), hemaInfo.getLongitude()));

            unmapped.put("HEMA", hemaInfo.getSummitCode());
        } else {
            logger.warning(String.format("Suspicious HEMA reference %s for callsign %s at %s", hemaId, rec.getCall(), rec.getTimeOn().toString()));
        }
    }

    private void setCoordFromPotaId(Adif3Record rec, String potaId, Map<String, String> unmapped) {
        // Upload latitude and longitude based on SOTA reference
        PotaInfo parkInfo = summits.getPota().get(potaId);
        if (parkInfo != null) {
            if (rec.getGridsquare() != null) {
                if (parkInfo.hasCoord()) {
                    GlobalCoordinates coord = new GlobalCoordinates(parkInfo.getLatitude(), parkInfo.getLongitude());
                    rec.setCoordinates(coord);
                } else if (parkInfo.hasGrid()) {
                    // Some parks don't have a grid
                    LatLng latLng = MaidenheadLocatorConversion.locatorToLatLng(parkInfo.getGrid());
                    GlobalCoordinates coords = new GlobalCoordinates(latLng.latitude, latLng.longitude);
                    rec.setMyCoordinates(coords);
                    rec.setGridsquare(parkInfo.getGrid());
                }
            }
            unmapped.put("POTA", parkInfo.getReference());
        } else {
            logger.warning(String.format("Suspicious POTA reference %s for callsign %s at %s", potaId, rec.getCall(), rec.getTimeOn().toString()));
        }
    }

    private void setMyLocationFromHemaId(Adif3Record rec, String hemaId) {
        // Upload latitude and longitude based on SOTA reference
        HemaSummitInfo hemaInfo = summits.getHema().get(hemaId);
        if (hemaInfo != null) {
            GlobalCoordinates coord = new GlobalCoordinates(hemaInfo.getLatitude(), hemaInfo.getLongitude());
            rec.setMyCoordinates(coord);

            // Also set the GridSquare as a fallback
            rec.setMyGridSquare(MaidenheadLocatorConversion.latLngToLocator(hemaInfo.getLatitude(), hemaInfo.getLongitude()));
        } else {
            logger.warning(String.format("Suspicious HEMA reference %s for your callsign %s", hemaId, rec.getStationCallsign()));
        }
    }

    private void setMyLocationFromPotaId(Adif3Record rec, String potaId) {
        // We treat POTA Grid references specified on the command line differently, as some parks don't have a grid reference
        // so an override take preference over everything
        PotaInfo potaInfo = summits.getPota().get(potaId);
        if (potaId != null) {
            if (potaInfo.hasCoord()) {
                GlobalCoordinates coord = new GlobalCoordinates(potaInfo.getLatitude(), potaInfo.getLongitude());
                rec.setMyCoordinates(coord);
            } else if (potaInfo.hasGrid()) {
                // Also set the GridSquare as a fallback
                rec.setMyGridSquare(MaidenheadLocatorConversion.latLngToLocator(potaInfo.getLatitude(), potaInfo.getLongitude()));
                rec.setMyCoordinates(new GlobalCoordinates(potaInfo.getLatitude(), potaInfo.getLongitude()));
            }
        } else {
            logger.warning(String.format("Suspicious Parks on the Air reference %s for your callsign %s", potaId, rec.getStationCallsign()));
        }
    }

    private void setMyLocationFromSotaId(Adif3Record rec, String sotaId) {
        // Brilliant, a SOTA reference is great for setting my location
        SotaSummitInfo sotaInfo = summits.getSota().get(sotaId);
        if (sotaInfo != null) {
            GlobalCoordinates coord = new GlobalCoordinates(sotaInfo.getLatitude(), sotaInfo.getLongitude());
            rec.setMyCoordinates(coord);
            if (rec.getMyGridSquare() == null) {
                rec.setMyGridSquare(MaidenheadLocatorConversion.latLngToLocator(sotaInfo.getLatitude(), sotaInfo.getLongitude()));
            }
        } else {
            logger.warning(String.format("Suspicious SOTA reference %s for YOU!", sotaId));
        }
    }

    private void setMyLocationFromGrid(Qso qso, String myGrid) {
        Adif3Record rec = qso.getRecord();
        qso.getRecord().setMyGridSquare(myGrid.substring(4));
        LatLng myLocation = MaidenheadLocatorConversion.locatorToLatLng(myGrid);
        rec.setMyCoordinates(new GlobalCoordinates(myLocation.latitude, myLocation.longitude));
    }

    private void setHemaOrSotaFromWota(Station station, String wotaId) {
        station.setHemaId(summits.getWota().get(wotaId).getHemaId());
        station.setSotaId(summits.getWota().get(wotaId).getSotaId());
    }

    private void setWotaFromHemaId(Station station, String hemaId) {
        WotaSummitInfo info = summits.getWota().getFromHemaId(hemaId);
        if (info != null) {
            station.setWotaId(info.getWotaId());
        }
    }

    private void setWotaFromSotaId(Station station, String sotaId) {
        WotaSummitInfo info = summits.getWota().getFromSotaId(sotaId);
        if (info != null) {
            station.setWotaId(summits.getWota().getFromSotaId(sotaId).getWotaId());
        }
    }

    private QrzCallsign setMyLocation(Qso qso) {
        Adif3Record rec = qso.getRecord();
        // Attempt a lookup from QRZ.com
        QrzCallsign callsignData = qrzXmlService.getCallsignData(rec.getStationCallsign());
        boolean locationOverride = false;

        if (control.getSota() != null) {
            qso.getFrom().setSotaId(control.getSota());
            setWotaFromSotaId(qso.getFrom(), control.getSota().toUpperCase());
        }
        if (control.getWota() != null) {
            qso.getFrom().setWotaId(control.getWota().toUpperCase());
            setHemaOrSotaFromWota(qso.getFrom(), control.getWota().toUpperCase());
        }
        if (control.getHema() != null) {
            qso.getFrom().setHemaId(control.getHema().toUpperCase());
            setWotaFromHemaId(qso.getFrom(), control.getHema().toUpperCase());
        }
        if (control.getPota() != null) {
            qso.getFrom().setPotaId(control.getPota().toUpperCase());
        }

        if (control.getMyLatitude() != null && control.getMyLongitude() != null) {
            double latitude = Double.parseDouble(StringUtils.remove(control.getMyLatitude(),'\''));
            double longitude = Double.parseDouble(StringUtils.remove(control.getMyLongitude(),'\''));
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
            if (control.getSota() != null) {
                if (!locationOverride) {
                    setMyLocationFromSotaId(rec, control.getSota().toUpperCase());
                }
            } else if (control.getWota() != null) {
                String wotaId = control.getWota().toUpperCase();
                if (!locationOverride) {
                    setMyLocationFromWotaId(rec, control.getWota().toUpperCase());
                }
            } else if (control.getHema() != null) {
                if (!locationOverride) {
                    setMyLocationFromHemaId(rec, control.getHema().toUpperCase());
                }
            } else if (rec.getMySotaRef() != null) {
                if (!locationOverride) {
                    setMyLocationFromSotaId(rec, rec.getMySotaRef().getValue());
                }
                qso.getFrom().setSotaId(rec.getMySotaRef().getValue());
                setWotaFromSotaId(qso.getFrom(), rec.getMySotaRef().getValue());
            } else if (control.getPota() != null) {
                if (!locationOverride) {
                    setMyLocationFromPotaId(rec, control.getPota().toUpperCase());
                }
            } else {
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
                    LatLng myLoc = MaidenheadLocatorConversion.locatorToLatLng(rec.getMyGridSquare());
                    GlobalCoordinates coord = new GlobalCoordinates(myLoc.latitude, myLoc.longitude);
                    rec.setMyCoordinates(coord);
                }
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
        String callsign = rec.getCall().strip();
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
                } else if (rec.getGridsquare() == null && !invalidGridBasedLoc) {
                    rec.setGridsquare(callsignData.getGrid());
                }
            }
        }
    }

    private String stripPortable(String callsign) {
        int loc = callsign.lastIndexOf('/');
        if (loc == -1) {
            return callsign;
        }
        return callsign.substring(0, loc);
    }

    @Override
    public void transform(Qsos qsos, Adif3Record rec) {
        /* Add Adif3Record details to the Qsos meta structure */
        Qso qso = createQsoFromAdif3Record(qsos, rec);

        Map<String, String> unmapped = new HashMap<>();
        QrzCallsign myQrzData = setMyLocation(qso);
        qso.getFrom().setQrzInfo(myQrzData);

        /* Load QRZ.COM info for the worked station as a fixed station, for information */
        String callsignToLookup = stripPortable(qso.getTo().getCallsign());
        QrzCallsign theirQrzData = qrzXmlService.getCallsignData(callsignToLookup);
        qso.getTo().setQrzInfo(theirQrzData);

        // Duplicate references into the comment
        if (rec.getSotaRef() != null) {
            String sotaId = rec.getSotaRef().getValue();
            unmapped.put("SOTA", sotaId);
            setCoordFromSotaId(rec, sotaId, unmapped);
            qso.getTo().setSotaId(sotaId);
        }

        if (StringUtils.isNotBlank(rec.getComment())) {
            transformComment(qso, rec.getComment(), unmapped);
        }
        if (rec.getGridsquare() == null) {
            theirQrzData = lookupLocationFromQrz(rec);
            qso.getTo().setQrzInfo(theirQrzData);
        } else if (rec.getCoordinates() == null) {
            // Set Coordinates from GridSquare that has been supplied in the input file
            LatLng loc = MaidenheadLocatorConversion.locatorToLatLng(rec.getGridsquare());
            GlobalCoordinates coord = new GlobalCoordinates(loc.latitude, loc.longitude);
            rec.setCoordinates(coord);
        }

        // Look to see if there is anything in the SIG/SIGINFO fields
        if (StringUtils.isNotEmpty(rec.getSig())) {
            processSig(qso);
        }

        if (!unmapped.isEmpty()) {
            addUnmappedToRecord(rec, unmapped);
        } else {
            // done a good job and slotted all the key/value pairs in the right place
            rec.setComment("");
        }
    }

    private void processSig(Qso qso) {
        Adif3Record rec = qso.getRecord();
        String sig = rec.getSig().toUpperCase();
        String sigInfo = rec.getSigInfo().toUpperCase();

        if (StringUtils.isNotEmpty(sigInfo)) {
            if (StringUtils.equals(sig, "POTA") && summits.getPota().get(sigInfo) != null) {
                // They are at a Park
                qso.getTo().setPotaId(sigInfo);
            } else if (StringUtils.equals(sig, "SOTA") && summits.getSota().get(sigInfo) != null) {
                // They are on a SOTA summit
                qso.getTo().setSotaId(sigInfo);
            } else if (StringUtils.equals(sig, "WOTA") && summits.getWota().get(sigInfo) != null) {
                // They are on a Wainwright
                qso.getTo().setWotaId(sigInfo);
            } else if (StringUtils.equals(sig, "HEMA") && summits.getHema().get(sigInfo) != null) {
                // They are on a HEMA summit
                qso.getTo().setHemaId(sigInfo);
            }
        }
    }

    private Qso createQsoFromAdif3Record(Qsos qsos, Adif3Record rec) {
        Qso qso = new Qso();
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
                            rec.setGridsquare(value.substring(0,6));
                        }
                        LatLng loc = MaidenheadLocatorConversion.locatorToLatLng(value);
                        GlobalCoordinates coord = new GlobalCoordinates(loc.latitude, loc.longitude);
                        rec.setCoordinates(coord);
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
                            setCoordFromSotaId(rec, sotaRef, unmapped);
                            qso.getTo().setSotaId(sotaRef);
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
                        setCoordFromWotaId(rec, wotaId.toUpperCase(), unmapped);
                        qso.getTo().setWotaId(wotaId);
                        break;
                    case "HemaRef":
                        // Strip off any S2s reference
                        String hemaId = StringUtils.split(value, ' ')[0];
                        setCoordFromHemaId(rec, hemaId.toUpperCase(), unmapped);
                        qso.getTo().setHemaId(hemaId);
                        break;
                    case "PotaRef":
                        // Strip off any S2s reference
                        String potaId = StringUtils.split(value, ' ')[0];
                        setCoordFromPotaId(rec, potaId.toUpperCase(), unmapped);
                        qso.getTo().setPotaId(potaId);
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
                }
            } else {
                unmapped.put(key, value);
            }
            issueWarnings(rec);
        }
        if (latitude != null && longitude != null) {
            rec.setCoordinates(new GlobalCoordinates(latitude, longitude));
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
            sb.append(String.format("%s: %s", key, unmapped.get(key)));
            if (i++ < keySetLen) {
                sb.append(", ");
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
