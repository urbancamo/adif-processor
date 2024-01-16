package uk.m0nom.adifproc.kml.info;

import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import org.thymeleaf.context.Context;
import uk.m0nom.adifproc.comms.CommsLinkResult;

import static uk.m0nom.adifproc.util.FrequencyFormatter.formatFrequency;

public class KmlBaseInfoPanel {
    /** One Hz in MHz */
    protected static final double ONE_HZ = 1.0/1000000.0;
    protected static final double MILES_PER_KM = 0.621371;

    protected void setBaseInfo(Adif3Record rec, Context context, CommsLinkResult commsLink) {
        if (rec.getMode() != null) {
            context.setVariable("mode", rec.getMode().toString());
        }
        if (rec.getFreq() != null) {
            context.setVariable("freq", formatFrequency(rec.getFreq()));
            /* Only display uplink/downlink frequencies separately if they differ by more than 1 Hz*/
            if (rec.getFreqRx() != null && Math.abs(rec.getFreq() - (rec.getFreqRx())) >= ONE_HZ) {
                context.setVariable("downlinkFreq", formatFrequency(rec.getFreqRx()));
            }
        }
        if (rec.getTxPwr() != null) {
            context.setVariable("txPwr", String.format("%,.1f", rec.getTxPwr()));
            if (commsLink != null) {
                context.setVariable("txPwrDistKmRatio", calculateWattsPerKmForDisplay(rec.getTxPwr(), commsLink.getDistanceInKm()));
                context.setVariable("txPwrDistMileRatio", calculateWattsPerMileForDisplay(rec.getTxPwr(), commsLink.getDistanceInKm()));
            }
        }
        if (rec.getRxPwr() != null) {
            context.setVariable("rxPwr", String.format("%,.1f", rec.getRxPwr()));
            if (commsLink != null) {
                context.setVariable("rxPwrDistKmRatio", calculateWattsPerKmForDisplay(rec.getRxPwr(), commsLink.getDistanceInKm()));
                context.setVariable("rxPwrDistMileRatio", calculateWattsPerMileForDisplay(rec.getRxPwr(), commsLink.getDistanceInKm()));
            }
        }
        if (rec.getBand() != null) {
            if (rec.getBandRx() != null) {
                context.setVariable("uplinkBand", StringUtils.replace(rec.getBand().name(), "BAND_", "").toLowerCase());
                context.setVariable("downlinkBand", StringUtils.replace(rec.getBandRx().name(), "BAND_", "").toLowerCase());
            } else {
                context.setVariable("band", StringUtils.replace(rec.getBand().name(), "BAND_", "").toLowerCase());
            }
        }
        if (commsLink != null) {
            context.setVariable("gndDist", String.format("%,.0f", commsLink.getDistanceInKm()));
            context.setVariable("gndDistMiles", String.format("%,.0f", commsLink.getDistanceInKm() * MILES_PER_KM));
        }
    }

    private String calculateWattsPerKmForDisplay(Double txPwr, double distanceInKm) {
        if (txPwr == null) {
            return null;
        }
        double wattsPerKm = txPwr / distanceInKm;
        if (wattsPerKm < 0.001) {
            return String.format("%,.0f uw/km", wattsPerKm * 1000000.0);
        } else if (wattsPerKm < 1) {
            return String.format("%,.0f mw/km", wattsPerKm * 1000.0);
        } else {
            return String.format("%,.1f w/km", wattsPerKm);
        }
    }


    private String calculateWattsPerMileForDisplay(Double txPwr, double distanceInKm) {
        if (txPwr == null) {
            return null;
        }
        double wattsPerMile = txPwr / distanceInKm / MILES_PER_KM;
        if (wattsPerMile < 0.001) {
            return String.format("%,.0f uw/mile", wattsPerMile * 1000000.0);
        } else if (wattsPerMile < 0.1) {
            return String.format("%,.0f mw/mile", wattsPerMile * 1000.0);
        } else {
            return String.format("%,.1f w/mile", wattsPerMile);
        }
    }
}
