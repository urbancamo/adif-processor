package uk.m0nom.adifproc.comms.ionosphere;

import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adifproc.comms.PropagationApex;
import uk.m0nom.adifproc.comms.PropagationModePredictor;

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
    public final static double HF_ANTENNA_DEFAULT_TAKEOFF_ANGLE = 6.0;

    private final Map<String, IonosphericLayer> dayTimeLayers;
    private final Map<String, IonosphericLayer> nightTimeLayers;

    /* Height at which we map ground wave communications, per 1000m */
    private final static double GROUNDWAVE_BOUNCE_ALT = 12.0;
    public final static double MAXIMUM_GROUND_WAVE_DISTANCE_HIGH_BANDS_KM = 500.0;
    public final static double MAXIMUM_GROUND_WAVE_DISTANCE_LOW_BANDS_KM = 50.0;

    public final static double MAXIMUM_USABLE_FREQUENCY = 22000.0;

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
    public List<PropagationApex> getBounces(Propagation mode, double frequencyInKhz, double distanceInKm, LocalTime timeOfDay,
                                            double myAltitude, double theirAltitude, double hfAntennaTakeoffAngle) {
        List<PropagationApex> bounces = new LinkedList<>();

        if (mode == null) {
            mode = PropagationModePredictor.predictPropagationMode(frequencyInKhz, distanceInKm);
        }
        if (mode != null) {
            Map<String, IonosphericLayer> layers;
            IonosphericLayer bounceLayer;
            double altInKm = 0L;

            switch (mode) {
                case F2_REFLECTION:
                    layers = getLayerForTimeOfDay(timeOfDay);
                    bounceLayer = layers.get("F2");

                    // Here we take into account that higher frequency signals tend to bounce at a lower height in the
                    // atmosphere than higher frequency signals
                    double altInMetres = calculateBounceHeight(frequencyInKhz, bounceLayer);
                    altInKm = altInMetres / 1000.0;

                    // We initially calculate the distance across the earth of propagation based on the takeoff angle
                    // this is then used to determine the number of hops required
                    PropagationApex apexResult = IonosphericApexCalculator.calculateDistanceOfApex(altInKm, hfAntennaTakeoffAngle);

                    int hops = calculateNumberOfHops(distanceInKm, apexResult);
                    // We now need to adjust the hops so that the fall equally between the stations
                    apexResult = adjustApexBasedOnHops(distanceInKm, apexResult, hops);


                    for (int i = 0; i < hops; i++) {
                        double hopDistance = apexResult.getDistanceAcrossEarth() * 2.0;
                        PropagationApex bounce = new PropagationApex(mode, hopDistance, apexResult.getDistanceToApex()*1000, altInKm*1000, 0, apexResult.getRadiationAngle());
                        bounces.add(bounce);
                    }
                    if (myAltitude > 0.0) {
                        bounces.get(0).setBaseHeight(myAltitude);
                    }
                    if (theirAltitude > 0.0) {
                        bounces.get(hops-1).setBaseHeight(theirAltitude);
                    }
                    break;
                case SPORADIC_E:
                    layers = getLayerForTimeOfDay(timeOfDay);
                    bounceLayer = layers.get("E");
                    altInKm = bounceLayer.getAverageHeight() / 1000.0;
                    bounces.add(new PropagationApex(mode, distanceInKm, altInKm*1000.0, altInKm*1000.0, 0, 0.0));
                    break;
            }
        }

        if (mode == null || bounces.isEmpty()) {
            // Single hop with nominal altitude that increases as the distance increases
            // this provides a very rough approximation of the way signals curve to follow the earth
            double adjustAlt = Math.max(myAltitude, theirAltitude);
            double apexHeight = Math.max(GROUNDWAVE_BOUNCE_ALT * distanceInKm, adjustAlt);
            PropagationApex bounce = new PropagationApex(null, distanceInKm, apexHeight, apexHeight, 0, 0.0);
            bounces.add(bounce);
        }
        return bounces;
    }

    private PropagationApex adjustApexBasedOnHops(double distanceAcrossEarthInKm, PropagationApex apexResult, int hopCount) {
        /* find the takeoff angle based on the distance between stations */

        // A bounce includes both up and down, so the apex distance is half the distance across the earth between stations
        double apexDistance = distanceAcrossEarthInKm / 2.0;

        double takeOffAngle = IonosphericApexCalculator.calculateTakeoffAngleFromDistanceAcrossEarth(apexDistance / hopCount, apexResult.getApexHeight());

        /* recalculate apex based on new takeOffAngle */
        return IonosphericApexCalculator.calculateDistanceOfApex(apexResult.getApexHeight(), takeOffAngle);
    }


    private double calculateBounceHeight(double frequencyInKhz, IonosphericLayer bounceLayer) {
        double bounceHeight = bounceLayer.getLower();
        if (frequencyInKhz < Ionosphere.MAXIMUM_USABLE_FREQUENCY && frequencyInKhz > (double) 1800) {
            // Normalize frequencies between 14Mhz and 1.8mhz to within 0.0 to 1.0
            double delta = (frequencyInKhz - (double) 1800) / (Ionosphere.MAXIMUM_USABLE_FREQUENCY - (double) 1800);
            double layerWidth = bounceLayer.getUpper() - bounceLayer.getLower();
            bounceHeight = bounceLayer.getLower() + (delta * layerWidth);
        } else if (frequencyInKhz < (double) 1800) {
            bounceHeight = bounceLayer.getUpper();
        }
        return bounceHeight;
    }


    private int calculateNumberOfHops(double distanceInKm, PropagationApex apexResult) {
        double singleHopDistance = apexResult.getDistanceAcrossEarth() * 2;
        return (int) Math.floor(distanceInKm / singleHopDistance) + 1;
    }


    private Map<String, IonosphericLayer> getLayerForTimeOfDay(LocalTime timeOfDay) {
        if (timeOfDay.isAfter(LocalTime.of(18,0)) && timeOfDay.isBefore(LocalTime.of(8,0))) {
            return nightTimeLayers;
        }
        return dayTimeLayers;
    }
}
