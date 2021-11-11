package uk.m0nom.kml.info.velocity;

import org.apache.commons.lang3.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkResult;
import uk.m0nom.geodesic.GeodesicUtils;
import uk.m0nom.kml.info.IKmlContactInfoPanel;

import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

public class VelocityKmlContactInfoPanel implements IKmlContactInfoPanel {
    @Override
    public String getPanelContentForCommsLink(TransformControl control, Qso qso, CommsLinkResult result) {
        Adif3Record rec = qso.getRecord();
        VelocityEngine ve = new VelocityEngine(control.getVelocityProperties());
        ve.init();
        Template t = ve.getTemplate("./kml/contact/KmlContactInfo.vm", StandardCharsets.UTF_8.name());
        VelocityContext context = new VelocityContext();
        context.put("qsoDate", rec.getQsoDate().toString());
        context.put("qsoTime", rec.getTimeOn().toString());
        context.put("call", rec.getCall());
        context.put("stationCallsign", rec.getStationCallsign());


        if (rec.getBand() != null) {
            if (rec.getBandRx() != null) {
                context.put("uplinkBand", StringUtils.replace(rec.getBand().name(), "BAND_", "").toLowerCase());
                context.put("downlinkBand", StringUtils.replace(rec.getBandRx().name(), "BAND_", "").toLowerCase());
            } else {
                context.put("band", StringUtils.replace(rec.getBand().name(), "BAND_", "").toLowerCase());
            }
        }
        if (rec.getMode() != null) {
            context.put("mode", rec.getMode().toString());
        }
        if (rec.getFreq() != null) {
            if (rec.getFreqRx() != null) {
                context.put("freq", String.format("%.3f", rec.getFreq()));
                context.put("downlinkFreq", String.format("%.3f", rec.getFreqRx()));
            } else {
                context.put("freq", String.format("%.3f", rec.getFreq()));
            }
        }
        if (rec.getTxPwr() != null) {
            context.put("txPwr", String.format("%.1f", rec.getTxPwr()));
        }
        context.put("gndDist", String.format("%.0f", result.getDistance()));
        Double bearing = GeodesicUtils.getBearing(rec.getMyCoordinates(), rec.getCoordinates());
        if (bearing != null) {
            context.put("bearing", String.format("%03.03f", bearing));
        }
        if (result.getMode() != null) {
            switch (result.getMode()) {
                case F2_REFLECTION:
                    context.put("skyDist", String.format("%.0f", result.getSkyDistance()));
                    context.put("bounces", String.format("%d", result.getBounces()));
                    if (result.getAltitude() > 9999.99) {
                        context.put("avgAlt", String.format("%.0f km", result.getAltitude() / 1000));
                    } else {
                        context.put("avgAlt", String.format("%.0f metres", result.getAltitude()));
                    }
                    context.put("avgAngle", String.format("%.0f°", result.getFromAngle()));
                    break;
                case SATELLITE:
                    context.put("satName", qso.getRecord().getSatName());
                    /*sb.append(String.format("Sky dist: %.0f km<br/>", result.getSkyDistance()));*/
                    context.put("satAlt", String.format("%.0f", result.getAltitude() / 1000));
                    break;
                case TROPOSPHERIC_DUCTING:
                    context.put("bounces", String.format("%d", result.getBounces()));
                    context.put("ductTop", String.format("%.0f", result.getAltitude()));
                    context.put("ductBase", String.format("%.0f", result.getBase()));
                    break;
            }
        }
        String mode = (result.getMode() != null) ? result.getMode().toString() : "GROUND WAVE";
        context.put("propagationMode", mode);
        
        StringWriter sw = new StringWriter();
        t.merge( context, sw );
        return sw.toString();
    }
}
