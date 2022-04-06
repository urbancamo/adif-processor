package uk.m0nom.adifproc.comms.ionosphere;

import org.jetbrains.annotations.NotNull;
import uk.m0nom.adifproc.comms.PropagationApex;

/**
 * This code assumes a spherical model of the Earth
 */
public class IonosphericApexCalculator {
    /** Earth average radius in KM */
    public final static double R = 6371;

    public static PropagationApex calculateDistanceOfApex(double h, double hfAntennaTakeoffAngleInDegrees) {
        // Distance to Ionosphere based on Ionospheric Height and Angle of Propagation from the Horizon
        double d = calculateDistanceToIonosphere(h, hfAntennaTakeoffAngleInDegrees);

        PropagationApex result = new PropagationApex();
        result.setApexHeight(h);
        result.setDistanceToApex(d);
        result.setRadiationAngle(hfAntennaTakeoffAngleInDegrees);

        // Now calculate distance across the surface of the Earth
        double distance = calculateDistanceAcrossEarth(result);
        result.setDistanceAcrossEarth(distance);

        return result;
    }

    public static double calculateTakeoffAngleFromDistanceAcrossEarth(double de, double h) {
        double takeOffAngle;


        // Calculate angle for arc of length d which is the distance across the earth, in degrees
        double A = (de / (2 * Math.PI * R)) * 360;

        // Using the cosine rule determine the distance of radiation to the ionosphere
        // a^2 = b^c + c^2 - 2bc cos A
        double b = R+h;
        double b2 = Math.pow(b, 2);
        double c = R;
        double c2 = Math.pow(c, 2);

        double a2 = b2 + c2 - (2 * b * c * Math.cos(Math.toRadians(A)));

        // distance to the ionosphere
        double a = Math.sqrt(a2);

        // Now calculate the angle of takeoff using the sine rule, choosing solution 2
        double B = 180 - Math.toDegrees(Math.asin((b * Math.sin(Math.toRadians(A))) / a));

        takeOffAngle = B - 90;

        return takeOffAngle;
    }

    public static double calculateDistanceToIonosphere(double h, double hfAntennaTakeoffAngleInDegrees) {
        // Calculate angle C from angle B and lengths b and c
        double B = hfAntennaTakeoffAngleInDegrees + 90.0;
        double c = R;
        double b = R + h;

        // Now calculate the sky return angle
        double C = Math.toDegrees(Math.asin((c * Math.sin(Math.toRadians(B))) / b));

        // As all angles add up to 180 degrees we can work out the earth chord angle
        double A = 180 - B - C;

        // Now use the Sine rule again to work out the sky distance

        return (c * Math.sin(Math.toRadians(A)) / Math.sin(Math.toRadians(C)));
    }

    @Deprecated
    public static double calculateDistanceToIonosphere2(double h, double hfAntennaTakeoffAngleInDegrees) {
        double a = Math.toRadians(hfAntennaTakeoffAngleInDegrees);
        double sinA2 = Math.pow(Math.sin(a), 2);
        double r2 = Math.pow(R, 2);
        double h2 = Math.pow(h, 2);

        return (-R*sinA2)+Math.sqrt((r2*sinA2)+h2+(2*R*h));
    }


    /**
     * Apex must contain the take-off angle and the distance to Apex for this calculation to complete
     */
    public static double calculateDistanceAcrossEarth(@NotNull PropagationApex apex) {
        double pi = Math.PI;
        double h = apex.getApexHeight();
        double theta = apex.getRadiationAngle();
        double d = apex.getDistanceToApex();

        // find angle of segment
        double a = (R+h) / Math.toDegrees(Math.sin(Math.toRadians(90 + theta)));
        double A = Math.asin(Math.toRadians(d / a));
        // find distance across arc of earth
        return (Math.toDegrees(A) / 360.0) * 2.0 * pi * R;
    }
}
