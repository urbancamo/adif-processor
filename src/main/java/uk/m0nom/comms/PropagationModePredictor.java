package uk.m0nom.comms;

import org.marsik.ham.adif.enums.Propagation;

public class PropagationModePredictor {
    public static Propagation predictPropagationMode(double frequencyInKhz, double distanceInKm) {
        if (frequencyInKhz > 50000 && distanceInKm < Ionosphere.MAXIMUM_GROUND_WAVE_DISTANCE_HIGH_BANDS_KM) {
            return null;
        } else if (frequencyInKhz > 50000 && distanceInKm >= Ionosphere.MAXIMUM_GROUND_WAVE_DISTANCE_HIGH_BANDS_KM) {
            return Propagation.SPORADIC_E;
        } else if (frequencyInKhz < 50000 && frequencyInKhz > 7000 && distanceInKm < Ionosphere.MAXIMUM_GROUND_WAVE_DISTANCE_HIGH_BANDS_KM) {
            return null;
        } else if (frequencyInKhz < 7000 && distanceInKm < Ionosphere.MAXIMUM_GROUND_WAVE_DISTANCE_LOW_BANDS_KM) {
            return null;
        } else if (frequencyInKhz < 50000 && distanceInKm >= Ionosphere.MAXIMUM_GROUND_WAVE_DISTANCE_HIGH_BANDS_KM) {
            return Propagation.F2_REFLECTION;
        }
        return Propagation.F2_REFLECTION;
    }
}
