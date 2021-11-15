package uk.m0nom.kml.info.thymeleaf;

import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkResult;
import uk.m0nom.geodesic.GeodesicUtils;
import uk.m0nom.kml.info.IKmlContactInfoPanel;

public class ThymeleafKmlContactInfoPanel implements IKmlContactInfoPanel {
    @Override
    public String getPanelContentForCommsLink(TransformControl control, Qso qso, CommsLinkResult result) {
        Adif3Record rec = qso.getRecord();

        TemplateEngine templateEngine = new TemplateEngine();
        ClassLoaderTemplateResolver resolver = new ClassLoaderTemplateResolver();
        resolver.setPrefix("templates/");
        resolver.setTemplateMode(TemplateMode.HTML);
        resolver.setSuffix(".html");
        templateEngine.setTemplateResolver(resolver);

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
                context.setVariable("freq", String.format("%.3f", rec.getFreq()));
                context.setVariable("downlinkFreq", String.format("%.3f", rec.getFreqRx()));
            } else {
                context.setVariable("freq", String.format("%.3f", rec.getFreq()));
            }
        }
        if (rec.getTxPwr() != null) {
            context.setVariable("txPwr", String.format("%.1f", rec.getTxPwr()));
        }
        context.setVariable("gndDist", String.format("%.0f", result.getDistance()));
        Double bearing = GeodesicUtils.getBearing(rec.getMyCoordinates(), rec.getCoordinates());
        if (bearing != null) {
            context.setVariable("bearing", String.format("%03.03f", bearing));
        }
        if (result.getMode() != null) {
            switch (result.getMode()) {
                case F2_REFLECTION:
                    context.setVariable("skyDist", String.format("%.0f", result.getSkyDistance()));
                    context.setVariable("bounces", String.format("%d", result.getBounces()));
                    if (result.getAltitude() > 9999.99) {
                        context.setVariable("avgAlt", String.format("%.0f km", result.getAltitude() / 1000));
                    } else {
                        context.setVariable("avgAlt", String.format("%.0f metres", result.getAltitude()));
                    }
                    context.setVariable("avgAngle", String.format("%.0f°", result.getFromAngle()));
                    break;
                case SATELLITE:
                    context.setVariable("satName", qso.getRecord().getSatName());
                    /*sb.append(String.format("Sky dist: %.0f km<br/>", result.getSkyDistance()));*/
                    context.setVariable("satAlt", String.format("%.0f", result.getAltitude() / 1000));
                    break;
                case TROPOSPHERIC_DUCTING:
                    context.setVariable("bounces", String.format("%d", result.getBounces()));
                    context.setVariable("ductTop", String.format("%.0f", result.getAltitude()));
                    context.setVariable("ductBase", String.format("%.0f", result.getBase()));
                    break;
            }
        }
        String mode = (result.getMode() != null) ? result.getMode().toString() : "GROUND WAVE";
        context.setVariable("propagationMode", mode);

        final String html = templateEngine.process("KmlContactInfo", context);
        return html;
    }
}
