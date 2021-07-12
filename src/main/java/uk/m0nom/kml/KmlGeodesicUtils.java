package uk.m0nom.kml;

import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.*;
import org.marsik.ham.adif.enums.Band;
import uk.m0nom.propagation.Ionosphere;
import uk.m0nom.propagation.PropagationBounce;
import uk.m0nom.propagation.PropagationMode;

import java.time.LocalTime;
import java.util.List;

public class KmlGeodesicUtils
{
    private final static LocalTime MIDDAY = LocalTime.of(12,0);

    public static void getSurfaceLine(LineString hfLine, GlobalCoordinates startGc, GlobalCoordinates endGc) {
        hfLine.addToCoordinates(startGc.getLongitude(), startGc.getLatitude(), 0);
        hfLine.addToCoordinates(endGc.getLongitude(), endGc.getLatitude(), 0);
    }

    public static HfLineResult getHfLine(LineString hfLine, GlobalCoordinates startGc, GlobalCoordinates endGc, Ionosphere ionosphere, Double frequency, Band band, LocalTime timeOfDay, double myAltitude, double theirAltitude) {
        /* assume daytime propagation if we don't have a QSO time */
        HfLineResult result = new HfLineResult();

        LocalTime time = MIDDAY;
        if (timeOfDay != null) {
            time = timeOfDay;
        }

        double frequencyInKhz = 145 * 1000;

        /* Get Frequency from Band */
        if (frequency != null) {
            frequencyInKhz = frequency * 1000;
        }
        else if (band != null) {
                frequencyInKhz = (band.getLowerFrequency() + ((band.getUpperFrequency() - band.getLowerFrequency()) / 2.0)) * 1000.0;
        }
        GlobalCoordinates start = new GlobalCoordinates(startGc.getLatitude(), startGc.getLongitude());
        GlobalCoordinates end = new GlobalCoordinates(endGc.getLatitude(), endGc.getLongitude());

        GeodeticCalculator calculator = new GeodeticCalculator();
        GeodeticCurve curve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, start, end);
        double distance = curve.getEllipsoidalDistance();

        /** work out the ionospheric propagation for this contact */
        double distanceInKm = distance / 1000;
        result.setDistance(distanceInKm);

        double azimuth = curve.getAzimuth();
        double avgAltitude = 0.0;
        double avgAngle = 0.0;
        PropagationMode mode = null;
        List<PropagationBounce> bounces = ionosphere.getBounces(frequencyInKhz, distanceInKm, time, myAltitude, theirAltitude);

        double skyDistance = addBouncesToLineString(hfLine, bounces, start, end, azimuth, calculator);
        result.setSkyDistance(skyDistance);

        for (PropagationBounce bounce : bounces) {
            avgAltitude += bounce.getHeight();
            avgAngle += bounce.getAngle();
            mode = bounce.getMode();
        }
        result.setMode(mode);
        result.setAltitude(avgAltitude / bounces.size());
        result.setAngle(avgAngle / bounces.size());
        result.setBounces(bounces.size());

        return result;
    }

    private static double addBouncesToLineString(LineString hfLine, List<PropagationBounce> bounces, GlobalCoordinates start, GlobalCoordinates end, double initialAzimuth, GeodeticCalculator calculator) {
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
            double distanceOfHalfHop = distanceAcrossGlobal / 2.0;
            /* need to make sure we take into account both sides of the hop into space and back again */
            double halfCommsDistance = Math.sqrt((distanceOfHalfHop * distanceOfHalfHop) + (reflectionHeight * reflectionHeight));
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
                hfLine.addToCoordinates(rtn.getLongitude(), rtn.getLatitude(), 0);
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
}
