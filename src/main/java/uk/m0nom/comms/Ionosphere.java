package uk.m0nom.comms;

import org.marsik.ham.adif.enums.Propagation;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A model of the Ionosphere that defines the layers in the Ionosphere and how the reflection properties
 * of a signal based on the signal frequency.
 */
public class Ionosphere {
    public final static double HF_ANTENNA_DEFAULT_TAKEOFF_ANGLE = 6;

    private final Map<String, IonosphericLayer> dayTimeLayers;
    private final Map<String, IonosphericLayer> nightTimeLayers;

    /* Height at which we map ground wave comms, per 1000m */
    private final static double GROUNDWAVE_BOUNCE_ALT = 6;
    public final static double MAXIMUM_GROUND_WAVE_DISTANCE_HIGH_BANDS_KM = 200;
    public final static double MAXIMUM_GROUND_WAVE_DISTANCE_LOW_BANDS_KM = 50;

    public Ionosphere() {
        dayTimeLayers = new HashMap<>();
        dayTimeLayers.put("D", new IonosphericLayer("D", metersFromKm(48), metersFromKm(90)));
        dayTimeLayers.put("E", new IonosphericLayer("E", metersFromKm(90), metersFromKm(150)));
        dayTimeLayers.put("F1", new IonosphericLayer("F1", metersFromKm(150), metersFromKm(250)));
        dayTimeLayers.put("F2", new IonosphericLayer("F2", metersFromKm(250), metersFromKm(500)));

        nightTimeLayers = new HashMap<>();
        nightTimeLayers.put("D", new IonosphericLayer("D", metersFromKm(48), metersFromKm(90)));
        nightTimeLayers.put("E", new IonosphericLayer("E", metersFromKm(90), metersFromKm(150)));
        dayTimeLayers.put("F1", new IonosphericLayer("F1", metersFromKm(150), metersFromKm(250)));
        dayTimeLayers.put("F2", new IonosphericLayer("F2", metersFromKm(250), metersFromKm(500)));
    }

    private double metersFromKm(double kms) {
        return kms * 1000;
    }

    /*
     * Get the number of bounces that a signal makes as it travels between the two stations.
     */
    public List<PropagationBounce> getBounces(Propagation mode, double frequencyInKhz, double distanceInKm, LocalTime timeOfDay,
                                              double myAltitude, double theirAltitude, double hfAntennaTakeoffAngle) {
        List<PropagationBounce> bounces = new LinkedList<>();

        if (mode == null) {
            mode = PropagationModePredictor.predictPropagationMode(frequencyInKhz, distanceInKm);
        }
        if (mode != null) {
            switch (mode) {
                case F2_REFLECTION:
                    Map<String, IonosphericLayer> layers = getLayerForTimeOfDay(timeOfDay);
                    IonosphericLayer bounceLayer = layers.get("F2");

                    // Here we take into account that higher frequency signals tend to bounce at a lower height in the
                    // atmosphere than higher frequency signals
                    double alt = calculateBounceHeight(frequencyInKhz, bounceLayer, 22000);

                    int hops = calculateNumberOfHops(distanceInKm, alt / 1000, hfAntennaTakeoffAngle);
                    for (int i = 0; i < hops; i++) {
                        double hopDistance = distanceInKm / hops;
                        PropagationBounce bounce = new PropagationBounce(mode, hopDistance, alt, 0, 0.0);
                        bounces.add(bounce);
                    }
                    break;
                case SPORADIC_E:
                    layers = getLayerForTimeOfDay(timeOfDay);
                    bounceLayer = layers.get("E");
                    alt = bounceLayer.getAverageHeight();
                    bounces.add(new PropagationBounce(mode, distanceInKm, alt, 0, 0.0));
                    break;
            }
        }

        if (mode == null || bounces.size() == 0) {
            // Single hop with nominal altitude that increases as the distance increases
            // this provides a very rough approximation of the way signals curve to follow the earth
            double adjustAlt = Math.max(myAltitude, theirAltitude);
            double apexHeight = Math.max(GROUNDWAVE_BOUNCE_ALT * distanceInKm, adjustAlt);
            PropagationBounce bounce = new PropagationBounce(null, distanceInKm, apexHeight, 0, 0.0);
            bounces.add(bounce);
        }
        return bounces;
    }

    private double calculateBounceHeight(double frequencyInKhz, IonosphericLayer bounceLayer, double maxFreq) {
        double bounceHeight = bounceLayer.getLower();
        if (frequencyInKhz < maxFreq && frequencyInKhz > (double) 1800) {
            // Normalize frequencies between 14Mhz and 1.8mhz to within 0.0 to 1.0
            double delta = (frequencyInKhz - (double) 1800) / (maxFreq - (double) 1800);
            double layerWidth = bounceLayer.getUpper() - bounceLayer.getLower();
            bounceHeight = bounceLayer.getLower() + (delta * layerWidth);
        } else if (frequencyInKhz < (double) 1800) {
            bounceHeight = bounceLayer.getUpper();
        }
        return bounceHeight;
    }

    /*
     * This is pure magic here, no science involved.
     */
    private int calculateNumberOfHops(double distanceInKm, double altInKm, double hfAntennaTakeoffAngle) {
        double angleOfRadiationSingleHop = 90.0 - Math.toDegrees(Math.atan(distanceInKm / altInKm));
        if (angleOfRadiationSingleHop < hfAntennaTakeoffAngle) {
            /* We need to break up the propagation into hops */
            return (int) (Math.floor(hfAntennaTakeoffAngle / angleOfRadiationSingleHop)) + 1;
        }
        return 1;
    }

    private Map<String, IonosphericLayer> getLayerForTimeOfDay(LocalTime timeOfDay) {
        if (timeOfDay.isAfter(LocalTime.of(18,0)) && timeOfDay.isBefore(LocalTime.of(8,0))) {
            return nightTimeLayers;
        }
        return dayTimeLayers;
    }
}
