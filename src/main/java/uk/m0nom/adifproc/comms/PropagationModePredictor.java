package uk.m0nom.adifproc.comms;

import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adifproc.comms.ionosphere.Ionosphere;

public class PropagationModePredictor {
    public static Propagation predictPropagationMode(double frequencyInKhz, double distanceInKm) {
        if (frequencyInKhz > Ionosphere.MAXIMUM_USABLE_FREQUENCY &&
                distanceInKm < Ionosphere.MAXIMUM_GROUND_WAVE_DISTANCE_HIGH_BANDS_KM) {
            return null;
        } else if (frequencyInKhz > Ionosphere.MAXIMUM_USABLE_FREQUENCY &&
                distanceInKm >= Ionosphere.MAXIMUM_GROUND_WAVE_DISTANCE_HIGH_BANDS_KM) {
            return Propagation.SPORADIC_E;
        } else if (frequencyInKhz < Ionosphere.MAXIMUM_USABLE_FREQUENCY &&
                frequencyInKhz > 15000 &&
                distanceInKm < Ionosphere.MAXIMUM_GROUND_WAVE_DISTANCE_HIGH_BANDS_KM) {
            return null;
        } else if (frequencyInKhz < 15000 &&
                distanceInKm < Ionosphere.MAXIMUM_GROUND_WAVE_DISTANCE_LOW_BANDS_KM) {
            return null;
        } else if (frequencyInKhz < Ionosphere.MAXIMUM_USABLE_FREQUENCY
                && distanceInKm >= Ionosphere.MAXIMUM_GROUND_WAVE_DISTANCE_HIGH_BANDS_KM) {
            return Propagation.F2_REFLECTION;
        }
        return Propagation.F2_REFLECTION;
    }
}
