package uk.m0nom.adif3;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.QslSent;
import org.marsik.ham.adif.enums.QslVia;
import org.marsik.ham.adif.types.Iota;
import org.marsik.ham.adif.types.Sota;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.maidenheadlocator.LatLng;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.qrz.QrzCallsign;
import uk.m0nom.qrz.QrzXmlService;
import uk.m0nom.sota.SotaSummitInfo;
import uk.m0nom.sota.SotaSummitsDatabase;
import uk.m0nom.summits.SummitsDatabase;
import uk.m0nom.wota.WotaSummitInfo;

import java.time.LocalDate;
import java.util.*;
import java.util.logging.Logger;

public class FastLogEntryAdifRecordTransformer implements Adif3RecordTransformer {
    private static final Logger logger = Logger.getLogger(FastLogEntryAdifRecordTransformer.class.getName());

    private YamlMapping fieldMap;
    private SummitsDatabase summits;
    private QrzXmlService qrzXmlService;
    private TransformControl control;

    private final String portableSuffixes[] = new String[] {"/P", "/M", "/MM", "/PM"};

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

    private void setMyLocation(Adif3Record rec) {
        if (rec.getMyCoordinates() == null) {
            if (rec.getMySotaRef() != null) {
                setMyLocationFromSotaId(rec, rec.getMySotaRef().getValue());
            } else {
                /* If user has supplied a maidenhead location, use that in preference */
                if (MaidenheadLocatorConversion.isAValidGridSquare(control.getMyGrid())) {
                    rec.setMyGridSquare(control.getMyGrid());
                }

                if (rec.getMyGridSquare() == null) {
                    // Attempt a lookup from QRZ.com
                    QrzCallsign callsignData = qrzXmlService.getCallsignData(rec.getStationCallsign());
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

    private void lookupLocationFromQrz(Adif3Record rec) {
        String callsign = rec.getCall();
        QrzCallsign callsignData = qrzXmlService.getCallsignData(callsign);
        if (callsignData != null) {
            logger.info(String.format("Updating location of station %s from QRZ.COM data", callsign));
           updateRecordFromQrzLocation(rec, callsignData);
        } else if (isPortable(callsign)) {
            // Try stripping off any portable callsign information and querying that as a last resort
            String fixedCallsign = callsign.substring(0, StringUtils.lastIndexOf(callsign, "/"));
            callsignData = qrzXmlService.getCallsignData(fixedCallsign);
            if (callsignData != null) {
                logger.info(String.format("Updating location of station %s from QRZ.COM FIXED station data %s", callsign, fixedCallsign));
                updateRecordFromQrzLocation(rec, callsignData);
            }
       }
    }

    /**
     * Attempt to update the location based on callsign data downloaded from QRZ.COM
     * What we need to be careful of here is of bad data. For example, some users set geoloc to
     * grid but then the grid isn't valid. We need to ignore that, or we'll set the station to
     * be antartica.
     *
     * @param rec
     * @param callsignData
     */
    private void updateRecordFromQrzLocation(Adif3Record rec, QrzCallsign callsignData) {
        if (callsignData != null) {
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

    @Override
    public void transform(Adif3Record rec) {
        Map<String, String> unmapped = new HashMap<>();
        setMyLocation(rec);
        // Duplicate references into the comment
        if (rec.getSotaRef() != null) {
            String sotaId = rec.getSotaRef().getValue();
            unmapped.put("SOTA", sotaId);
            setCoordFromSotaId(rec, sotaId, unmapped);
        } else if (rec.getGridsquare() == null) {
            lookupLocationFromQrz(rec);
        }
        if (StringUtils.isNotBlank(rec.getComment())) {
            transformComment(rec, rec.getComment(), unmapped);
        }
        if (!unmapped.isEmpty()) {
            addUnmappedToRecord(rec, unmapped);
        } else {
            // done a good job and slotted all the key/value pairs in the right place
            rec.setComment("");
        }
    }

    /**
     * Parse the Fast Log Entry comment string for pairs of key and values, for example
     * OP: John, QTH: Gatwick, PWR: 100W, ANT: Inv-V, WX: 4 degC, GRID: IO84io
     * In this case OP, QTH and PWR are transferred into their respective ADIF records,
     * and ANT/WX records are appended to the comment
     * @param rec
     * @param comment
     */
    private void transformComment(Adif3Record rec, String comment, Map<String, String> unmapped) {
        // try and split the comment up into comma separated list
        Map<String, String> tokens = tokenize(comment);

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
                            System.err.println(String.format("Couldn't parse IOTA field %s for call %s at %s, please check, leaving it unmapped", value, rec.getCall(), rec.getTimeOn()));
                            unmapped.put(key, value);
                        }
                        break;
                    case "GridSquare":
                        if (MaidenheadLocatorConversion.isAValidGridSquare(value)) {
                            rec.setGridsquare(value);
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
                            Sota sota = Sota.valueOf(sotaRef);
                            rec.setSotaRef(sota);
                            setCoordFromSotaId(rec, sotaRef, unmapped);
                        } catch (IllegalArgumentException iae) {
                            // something we can't work out about the reference, so put it in the unmapped list instead
                            System.err.println(String.format("Couldn't identify %s as a SOTA reference in field %s, leaving it unmapped", sotaRef, value));
                            unmapped.put(key, value);
                        }
                        // We also add the Sota reference as-is to the comment field
                        unmapped.put(key, value);
                        break;
                    case "WotaRef":
                        // Strip off any S2s reference
                        String wotaId = StringUtils.split(value, ' ')[0];
                        setCoordFromWotaId(rec, wotaId, unmapped);
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
                }
            } else {
                unmapped.put(key, value);
            }
            issueWarnings(rec);
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
                String pair[] = StringUtils.split(token, ":");
                tokens.put(pair[0].trim(), pair[1].trim());
            }
        }
        return tokens;
    }
}
