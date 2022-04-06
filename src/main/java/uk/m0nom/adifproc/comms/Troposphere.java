package uk.m0nom.adifproc.comms;

import org.marsik.ham.adif.enums.Propagation;

import java.util.LinkedList;
import java.util.List;

public class Troposphere {
    public final static double DUCT_ALTITUDE = 2000;
    public final static double DUCT_SIZE = 500;

    public Troposphere() {
    }

    private double metersFromKm(double kms) {
        return kms * 1000;
    }

    public List<PropagationApex> getBounces(double distanceInKm) {
        List<PropagationApex> bounces = new LinkedList<>();

        double topOfDuctAlt = DUCT_ALTITUDE + (DUCT_SIZE / 2);
        double bottomOfDuctAlt = DUCT_ALTITUDE - (DUCT_SIZE / 2);

        int hops = calculateNumberOfDuctBounces(metersFromKm(distanceInKm));
        double hopDistance = distanceInKm / hops;

        // First hop from my site to the duct
        for (int i = 0; i < hops; i++) {
            PropagationApex bounce = new PropagationApex(Propagation.TROPOSPHERIC_DUCTING, hopDistance, topOfDuctAlt, topOfDuctAlt, bottomOfDuctAlt,0.0);
            bounces.add(bounce);
        }
        return bounces;
    }

    /**
     * This is pure magic here, no science involved.
     */
    private int calculateNumberOfDuctBounces(double distance) {
        return (int) (Math.floor(distance / DUCT_SIZE / 16)) + 1;
    }
}
