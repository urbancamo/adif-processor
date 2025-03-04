package uk.m0nom.adifproc.adif3.transform;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.coords.LocationAccuracy;
import uk.m0nom.adifproc.coords.LocationSource;
import uk.m0nom.adifproc.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.adifproc.qrz.QrzCallsign;
import uk.m0nom.adifproc.qrz.QrzService;

import java.util.logging.Logger;

/**
 * Populate our output ADIF record wih information obtained from QRZ.COM
 */
@Service
@AllArgsConstructor
public class AdifQrzEnricher {
    private static final Logger logger = Logger.getLogger(AdifQrzEnricher.class.getName());

    private final QrzService cachingQrzXmlService;

    public static String getNameFromQrzData(QrzCallsign qrzData) {
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

    public void enrichAdifForMe(Adif3Record rec, QrzCallsign qrzData) {
        if (qrzData == null) {
            return;
        }
        // Now comes from DxccEntity computed from callsign
        // if (rec.getMyCountry() == null) {
        // rec.setMyCountry(qrzData.getCountry());
        // }
        if (rec.getMyName() == null) {
            rec.setMyName(getNameFromQrzData(qrzData));
        }
    }

    public void enrichAdifForThem(Adif3Record rec, QrzCallsign qrzData) {
        if (qrzData == null) {
            return;
        }

        // Now comes from DxccEntity computed from callsign
        //if (rec.getCountry() == null) {
        //    rec.setCountry(qrzData.getCountry());
        //}

        String qrzName = getNameFromQrzData(qrzData);
        if (rec.getName() == null) {
            rec.setName(qrzName);
        }

        if (rec.getQth() == null) {
            StringBuilder addr = new StringBuilder();
            if (StringUtils.isNotEmpty(qrzData.getAddr1())) {
                addr.append(qrzData.getAddr1());
                addr.append(", ");
            }
            if (StringUtils.isNotEmpty(qrzData.getAddr2())) {
                addr.append(qrzData.getAddr2());
            }
            rec.setQth(addr.toString());
        }

        if (rec.getState() == null) {
            rec.setState(qrzData.getState());
        }
    }

    public void lookupLocationFromQrz(Qso qso) {
        QrzCallsign callsignData = qso.getTo().getQrzInfo();
        Adif3Record rec = qso.getRecord();
        String callsign = rec.getCall();

        if (callsignData == null) {
            callsignData = cachingQrzXmlService.getCallsignData(callsign);
            qso.getTo().setQrzInfo(callsignData);
        }

        if (callsignData != null) {
            updateQsoFromQrzLocation(qso, callsignData);
            logger.info(String.format("Retrieved %s from QRZ.COM", callsign));
        } else if (CallsignUtils.isPortable(callsign)) {
            // Try stripping off any portable callsign information and querying that as a last resort
            String fixedCallsign = callsign.substring(0, StringUtils.lastIndexOf(callsign, "/"));
            callsignData = cachingQrzXmlService.getCallsignData(fixedCallsign);
            if (callsignData != null) {
                logger.info(String.format("Retrieved %s from QRZ.COM using FIXED station callsign %s", callsign, fixedCallsign));
                qso.getTo().setQrzInfo(callsignData);
                updateQsoFromQrzLocation(qso, callsignData);
            }
        }
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
                boolean geocodeBasedGeoLocation = StringUtils.equalsIgnoreCase("geocode", callsignData.getGeoloc());
                boolean gridBasedGeoLocation = StringUtils.equalsIgnoreCase("grid", callsignData.getGeoloc());
                boolean userGeoLocation = StringUtils.equalsIgnoreCase("user", callsignData.getGeoloc());

                String qrzGridSquare = callsignData.getGrid();
                boolean isAValidGridBasedLocation = (gridBasedGeoLocation || userGeoLocation) &&
                        MaidenheadLocatorConversion.isValid(qrzGridSquare);

                GlobalCoords3D coord = null;
                if (callsignData.getLat() != null && callsignData.getLon() != null && isAValidGridBasedLocation) {
                    coord = new GlobalCoords3D(callsignData.getLat(), callsignData.getLon(),
                            LocationSource.QRZ, LocationAccuracy.LAT_LONG);
                    if (geocodeBasedGeoLocation) {
                        coord.setLocationInfo(LocationSource.QRZ, LocationAccuracy.GEOLOCATION_GOOD);
                    } else if (gridBasedGeoLocation) {
                        coord.setLocationInfo(LocationSource.QRZ, LocationAccuracy.MHL6);
                    }
                } else if (rec.getGridsquare() == null && isAValidGridBasedLocation) {
                    // Fallback to qrz grid square if qrz data doesn't contain lat/long
                    coord = MaidenheadLocatorConversion.locatorToCoords(LocationSource.QRZ, qrzGridSquare);
                }

                if (coord != null) {
                    rec.setCoordinates(coord);
                    qso.getTo().setCoordinates(coord);
                }
            }
        }
    }
}
