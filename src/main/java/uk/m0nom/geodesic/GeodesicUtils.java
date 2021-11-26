package uk.m0nom.geodesic;

import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.*;
import uk.m0nom.comms.*;

import java.util.List;

public class GeodesicUtils
{
    public static void getSurfaceLine(LineString hfLine, GlobalCoordinates startGc, GlobalCoordinates endGc) {
        hfLine.addToCoordinates(startGc.getLongitude(), startGc.getLatitude(), 0);
        hfLine.addToCoordinates(endGc.getLongitude(), endGc.getLatitude(), 0);
    }


    public static double addBouncesToLineString(LineString hfLine, List<PropagationBounce> bounces, GlobalCoordinates start, GlobalCoordinates end,
                                                double initialAzimuth) {
        GeodeticCalculator calculator = new GeodeticCalculator();
        hfLine.addToCoordinates(start.getLongitude(), start.getLatitude(), 0);
        GlobalCoordinates previous = start;
        double azimuth = initialAzimuth;
        double skyDistance = 0.0;

        /* number of lines will be twice the number of bounces */
        for (int i = 0; i < bounces.size(); i++) {
            PropagationBounce bounce = bounces.get(i);

            /* Need work out the distance taking into account the altitude gain */

            double distanceAcrossGlobal = bounce.getDistance() * 1000;
            double reflectionHeight = bounce.getHeight();
            double baseHeight = bounce.getBase();
            double distanceOfHalfHop = distanceAcrossGlobal / 2.0;

            /* need to make sure we take into account both sides of the hop */
            double halfCommsDistance = Math.sqrt((distanceOfHalfHop * distanceOfHalfHop) + ((reflectionHeight * reflectionHeight) - (baseHeight * baseHeight)));
            skyDistance += halfCommsDistance * 2.0 / 1000.0;

            /* set the angle of the bounce */
            double angle = Math.toDegrees(Math.atan((halfCommsDistance / distanceOfHalfHop)));
            bounce.setAngle(angle);

            /* Add 'up' bounce */
            GlobalCoordinates apex = calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84, previous, azimuth, distanceAcrossGlobal / 2.0);
            hfLine.addToCoordinates(apex.getLongitude(), apex.getLatitude(), bounce.getHeight());

            /* Recalculate Azimuth between Apex and End Point */
            GeodeticCurve curve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, apex, end);
            azimuth = curve.getAzimuth();

            /* Handle the last return by working backwards from the end point, so we don't lose accuracy */
            if  (i == bounces.size() - 1) {
                hfLine.addToCoordinates(end.getLongitude(), end.getLatitude(), 0);
            } else {
                GlobalCoordinates rtn = calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84, apex, azimuth, distanceAcrossGlobal / 2.0);
                hfLine.addToCoordinates(rtn.getLongitude(), rtn.getLatitude(), baseHeight);
                /* Recalculate Azimuth between Apex and End Point */
                curve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, rtn, end);
                azimuth = curve.getAzimuth();
                previous = rtn;
            }
        }
        hfLine.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
        hfLine.setExtrude(false);

        return skyDistance;
    }

    private final static double EARTH_RADIUS_IN_METRES = 6378 * 1000;

    /**
     * It is assumed the Earth is round for this calculation, so there will be a margin of error.
     * As the result is used in a calculation of angle shown to the nearest degree, it's probably OK for now
     * This calculates the straight line distance between two points on the Earth, through the Earth, using the
     * geodesic distance on the surface of the Earth.
     *
     * @param geodesicDistance distance between two points traversing across the Earth surface
     * @return straight line distance between two points, direct, through the Earth.
     */
    public static double geodesicDistanceToStraightLineDistance(double geodesicDistance) {
        double r = EARTH_RADIUS_IN_METRES;

        double d = 2 * r * Math.sin(geodesicDistance / (2 * r));

        return d;
    }

    /**
     * Check that coordinates are equal or at least very close
     * @param myCoords my coordinates
     * @param coords their coordinates
     * @return true if the latitude and longitude are within 0.0001 of each other
     */
    public static boolean areCoordsEqual(GlobalCoordinates myCoords, GlobalCoordinates coords) {
        final double tolerance = 0.0001;
        return Math.abs(myCoords.getLatitude() - coords.getLatitude()) < tolerance && Math.abs(myCoords.getLongitude() - coords.getLongitude()) < tolerance;
    }

    public static Double getBearing(GlobalCoordinates myCoordinates, GlobalCoordinates coordinates) {
        Double bearing = null;
        if (myCoordinates != null && coordinates != null) {
            GeodeticCalculator calc = new GeodeticCalculator();
            GeodeticCurve curve = calc.calculateGeodeticCurve(Ellipsoid.WGS84, myCoordinates, coordinates);
            bearing = curve.getAzimuth();
        }
        return bearing;
    }
}
