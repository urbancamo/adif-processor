package uk.m0nom.kml.info;

import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.comms.CommsLinkResult;
import uk.m0nom.geodesic.GeodesicUtils;

import java.util.Locale;

public class KmlContactInfoPanel {
    public String getPanelContentForCommsLink(Qso qso, CommsLinkResult result) {
        Adif3Record rec = qso.getRecord();
        StringBuilder sb=  new StringBuilder();
        sb.append("<b>Contact</b><br/><br/><br/>");
        sb.append(String.format("D: %s, T: %s<br/>", rec.getQsoDate().toString(), rec.getTimeOn().toString()));
        sb.append(String.format("<a href=\"https://qrz.com/db/%s\">%s</a> ⇋ ",
                rec.getStationCallsign(), rec.getStationCallsign()));
        sb.append(String.format("<a href=\"https://qrz.com/db/%s\">%s</a><br/>",
                rec.getCall(), rec.getCall()));

        if (rec.getBand() != null) {
            if (rec.getBandRx() != null) {
                sb.append(String.format("Uplink Band: %s<br/>", StringUtils.replace(rec.getBand().name(), "BAND_", "").toLowerCase(Locale.ROOT)));
                sb.append(String.format("Downlink Band: %s<br/>", StringUtils.replace(rec.getBandRx().name(), "BAND_", "").toLowerCase(Locale.ROOT)));
            } else {
                sb.append(String.format("Band: %s<br/>", StringUtils.replace(rec.getBand().name(), "BAND_", "").toLowerCase(Locale.ROOT)));
            }
        }
        if (rec.getMode() != null) {
            sb.append(String.format("Mode: %s<br/>", rec.getMode().toString()));
        }
        if (rec.getFreq() != null) {
            if (rec.getFreqRx() != null) {
                sb.append(String.format("Uplink Freq: %.3f Mhz<br/>", rec.getFreq()));
                sb.append(String.format("Downlink Freq: %.3f Mhz<br/>", rec.getFreqRx()));
            } else {
                sb.append(String.format("Freq: %.3f Mhz<br/>", rec.getFreq()));
            }
        }
        if (rec.getTxPwr() != null) {
            sb.append(String.format("TX Pwr: %.1f Watts<br/>", rec.getTxPwr()));
        }
        sb.append(String.format("Gnd dist: %.0f km<br/>", result.getDistance()));
        Double bearing = GeodesicUtils.getBearing(rec.getMyCoordinates(), rec.getCoordinates());
        if (bearing != null) {
            sb.append(String.format("Bearing: %03.03f°<br/>", bearing));
        }
        if (result.getMode() != null) {
            switch (result.getMode()) {
                case F2_REFLECTION:
                    sb.append(String.format("Sky dist: %.0f km<br/>", result.getSkyDistance()));
                    sb.append(String.format("Bounces: %d<br/>", result.getBounces()));
                    if (result.getAltitude() > 9999.99) {
                        sb.append(String.format("Avg Alt: %.0f km<br/>", result.getAltitude() / 1000));
                    } else {
                        sb.append(String.format("Avg Alt: %.0f metres<br/>", result.getAltitude()));
                    }
                    sb.append(String.format("Avg Angle: %.0f°<br/>", result.getFromAngle()));
                    break;
                case SATELLITE:
                    sb.append(String.format("Satellite: %s<br/>", qso.getRecord().getSatName()));
                    /*sb.append(String.format("Sky dist: %.0f km<br/>", result.getSkyDistance()));*/
                    sb.append(String.format("Sat Alt: %.0f km<br/>", result.getAltitude() / 1000));
                    break;
                case TROPOSPHERIC_DUCTING:
                    sb.append(String.format("Bounces: %d<br/>", result.getBounces()));
                    sb.append(String.format("Duct Top: %.0f metres<br/>", result.getAltitude()));
                    sb.append(String.format("Duct Base: %.0f metres<br/>", result.getBase()));
                    break;
            }
        }
        String mode = (result.getMode() != null) ? result.getMode().toString() : "GROUND WAVE";
        sb.append(String.format("Propagation Mode: %s", mode));

        return sb.toString();
    }


}
