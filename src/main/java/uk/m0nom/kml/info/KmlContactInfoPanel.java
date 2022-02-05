package uk.m0nom.kml.info;

import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.AntPath;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.TemplateSpec;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkResult;
import uk.m0nom.geodesic.GeodesicUtils;

public class KmlContactInfoPanel {
    public String getPanelContentForCommsLink(TransformControl control, Qso qso, CommsLinkResult result, TemplateEngine templateEngine) {
        Adif3Record rec = qso.getRecord();

        final Context context = new Context();
        context.setVariable("qsoDate", rec.getQsoDate().toString());
        context.setVariable("qsoTime", rec.getTimeOn().toString());
        context.setVariable("call", rec.getCall());
        context.setVariable("stationCallsign", rec.getStationCallsign());


        if (rec.getBand() != null) {
            if (rec.getBandRx() != null) {
                context.setVariable("uplinkBand", StringUtils.replace(rec.getBand().name(), "BAND_", "").toLowerCase());
                context.setVariable("downlinkBand", StringUtils.replace(rec.getBandRx().name(), "BAND_", "").toLowerCase());
            } else {
                context.setVariable("band", StringUtils.replace(rec.getBand().name(), "BAND_", "").toLowerCase());
            }
        }
        if (rec.getMode() != null) {
            context.setVariable("mode", rec.getMode().toString());
        }
        if (rec.getFreq() != null) {
            if (rec.getFreqRx() != null) {
                context.setVariable("freq", String.format("%,.3f", rec.getFreq()));
                context.setVariable("downlinkFreq", String.format("%,.3f", rec.getFreqRx()));
            } else {
                context.setVariable("freq", String.format("%,.3f", rec.getFreq()));
            }
        }
        if (rec.getTxPwr() != null) {
            context.setVariable("txPwr", String.format("%,.1f", rec.getTxPwr()));
        }
        context.setVariable("gndDist", String.format("%,.0f", result.getDistanceInKm()));
        context.setVariable("azimuth", String.format("%03.03f", result.getAzimuth()));

        if (result.getPropagation() != null) {
            switch (result.getPropagation()) {
                case F2_REFLECTION:
                    context.setVariable("skyDist", String.format("%,.0f", result.getSkyDistance()));
                    context.setVariable("bounces", String.format("%d", result.getBounces()));
                    context.setVariable("antPath", rec.getAntPath() == null ? AntPath.SHORT : rec.getAntPath());
                    break;
                case SATELLITE:
                    context.setVariable("satName", qso.getRecord().getSatName());
                    /*sb.append(String.format("Sky dist: %,.0f km<br/>", result.getSkyDistance()));*/
                    context.setVariable("satAlt", String.format("%,.0f km", result.getAltitude() / 1000));
                    break;
                case TROPOSPHERIC_DUCTING:
                    context.setVariable("bounces", String.format("%d", result.getBounces()));
                    context.setVariable("ductTop", String.format("%,.0f", result.getAltitude()));
                    context.setVariable("ductBase", String.format("%,.0f", result.getBase()));
                    break;
            }
        }
        if (result.getAltitude() > 9999.99) {
            context.setVariable("avgAlt", String.format("%,.0f km", result.getAltitude() / 1000));
        } else {
            context.setVariable("avgAlt", String.format("%,.0f metres", result.getAltitude()));
        }
        context.setVariable("angle", String.format("%,.0f°", result.getFromAngle()));

        String mode = (result.getPropagation() != null) ? result.getPropagation().adifCode() : "GND";
        context.setVariable("propagationMode", mode);

        final String html = templateEngine.process(new TemplateSpec("KmlContactInfo", TemplateMode.XML), context);
        return html.replace("\n", "");
    }
}
