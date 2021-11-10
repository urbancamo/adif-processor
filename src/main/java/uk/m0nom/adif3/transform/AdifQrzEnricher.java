package uk.m0nom.adif3.transform;

import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;
import uk.m0nom.coords.LocationAccuracy;
import uk.m0nom.coords.LocationSource;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.qrz.QrzCallsign;
import uk.m0nom.qrz.QrzService;

import java.util.logging.Logger;

/**
 * Populate our output ADIF record wih information obtained from QRZ.COM
 */
public class AdifQrzEnricher {
    private static final Logger logger = Logger.getLogger(AdifQrzEnricher.class.getName());

    private final QrzService qrzService;

    public AdifQrzEnricher(QrzService qrzService) {
        this.qrzService = qrzService;
    }

    public void enrichAdifForMe(Adif3Record rec, QrzCallsign qrzData) {
        if (qrzData == null) {
            return;
        }

        if (rec.getMyCountry() == null) {
            rec.setMyCountry(qrzData.getCountry());
        }
        if (rec.getMyName() == null) {
            rec.setMyName(getNameFromQrzData(qrzData));
        }
    }

    public void enrichAdifForThem(Adif3Record rec, QrzCallsign qrzData) {
        if (qrzData == null) {
            return;
        }

        if (rec.getCountry() == null) {
            rec.setCountry(qrzData.getCountry());
        }

        if (rec.getName() == null) {
            rec.setName(getNameFromQrzData(qrzData));
        }

        if (rec.getQth() == null) {
            StringBuilder addr = new StringBuilder();
            if (qrzData.getAddr1() != null) {
                addr.append(qrzData.getAddr1());
                if (qrzData.getAddr2() != null) {
                    addr.append(", ");
                    addr.append(qrzData.getAddr2());
                }
            }
            rec.setQth(addr.toString());
        }
    }

    private String getNameFromQrzData(QrzCallsign qrzData) {
        String name = "";
        if (StringUtils.isNotEmpty(qrzData.getFname())) {
            name = qrzData.getFname();
        }
        if (StringUtils.isNotEmpty(qrzData.getName())) {
            if (StringUtils.isNotEmpty(name)) {
                name = name + " ";
            }
            name = name + qrzData.getName();
        }
        return name;
    }

    QrzCallsign lookupLocationFromQrz(Qso qso) {
        Adif3Record rec = qso.getRecord();
        String callsign = rec.getCall();
        QrzCallsign callsignData = qrzService.getCallsignData(callsign);
        if (callsignData != null) {
            updateQsoFromQrzLocation(qso, callsignData);
            logger.info(String.format("Retrieved %s from QRZ.COM", callsign));
        } else if (CallsignUtils.isNotFixed(callsign)) {
            // Try stripping off any portable callsign information and querying that as a last resort
            String fixedCallsign = callsign.substring(0, StringUtils.lastIndexOf(callsign, "/"));
            callsignData = qrzService.getCallsignData(fixedCallsign);
            if (callsignData != null) {
                logger.info(String.format("Retrieved %s from QRZ.COM using FIXED station callsign %s", callsign, fixedCallsign));
                updateQsoFromQrzLocation(qso, callsignData);
            }
        }
        return callsignData;
    }

    /**
     * Attempt to update the location based on callsign data downloaded from QRZ.COM
     * What we need to be careful of here is of bad data. For example, some users set geoloc to
     * grid but then the grid isn't valid. We need to ignore that, or we'll set the station to
     * be Antarctica.
     */
    private void updateQsoFromQrzLocation(Qso qso, QrzCallsign callsignData) {
        if (callsignData != null) {
            Adif3Record rec = qso.getRecord();
            if (rec.getCoordinates() == null) {
                boolean geocodeBasedGeoLoc = StringUtils.equalsIgnoreCase("geocode", callsignData.getGeoloc());
                boolean gridBasedGeoloc = StringUtils.equalsIgnoreCase("grid", callsignData.getGeoloc());

                String gridSquare = callsignData.getGrid();
                boolean invalidGridBasedLoc = gridBasedGeoloc && (!MaidenheadLocatorConversion.isAValidGridSquare(gridSquare) || MaidenheadLocatorConversion.isADubiousGridSquare(gridSquare));

                if (callsignData.getLat() != null && callsignData.getLon() != null && !invalidGridBasedLoc) {
                    GlobalCoordinatesWithSourceAccuracy coord = new GlobalCoordinatesWithSourceAccuracy(callsignData.getLat(), callsignData.getLon(),
                            LocationSource.QRZ, LocationAccuracy.LAT_LONG);
                    if (geocodeBasedGeoLoc) {
                        coord.setLocationInfo(LocationSource.QRZ, LocationAccuracy.GEOLOCATION_GOOD);
                    } else if (gridBasedGeoloc) {
                        coord.setLocationInfo(LocationSource.QRZ, LocationAccuracy.MHL6);
                    }
                    rec.setCoordinates(coord);
                    qso.getTo().setCoordinates(coord);
                }
                if (rec.getGridsquare() == null && !invalidGridBasedLoc) {
                    rec.setGridsquare(callsignData.getGrid());
                }
            }
        }
    }


}
