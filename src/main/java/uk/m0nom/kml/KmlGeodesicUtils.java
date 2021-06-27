package uk.m0nom.kml;

import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.*;
import org.marsik.ham.adif.enums.Band;
import uk.m0nom.ionosphere.Ionosphere;
import uk.m0nom.ionosphere.PropagationBounce;

import java.time.LocalTime;
import java.util.List;

public class KmlGeodesicUtils
{
    private final static LocalTime MIDDAY = LocalTime.of(12,0);

    public static void getSurfaceLine(LineString hfLine, GlobalCoordinates startGc, GlobalCoordinates endGc) {
        hfLine.addToCoordinates(startGc.getLongitude(), startGc.getLatitude(), 0);
        hfLine.addToCoordinates(endGc.getLongitude(), endGc.getLatitude(), 0);
    }

    public static void getHfLine(LineString hfLine, GlobalCoordinates startGc, GlobalCoordinates endGc, Ionosphere ionosphere, Double frequency, Band band, LocalTime timeOfDay) {
        /* assume daytime propagation if we don't have a QSO time */
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
        double azimuth = curve.getAzimuth();
        List<PropagationBounce> bounces = ionosphere.getBounces(frequencyInKhz, distanceInKm, time);
        addBouncesToLineString(hfLine, bounces, start, end, azimuth, calculator);
    }

    private static void addBouncesToLineString(LineString hfLine, List<PropagationBounce> bounces, GlobalCoordinates start, GlobalCoordinates end, double initialAzimuth, GeodeticCalculator calculator) {
        hfLine.addToCoordinates(start.getLongitude(), start.getLatitude(), 0);
        GlobalCoordinates previous = start;
        double azimuth = initialAzimuth;

        /* number of lines will be twice the number of bounces */
        for (int i = 0; i < bounces.size(); i++) {
            PropagationBounce bounce = bounces.get(i);

            /* Need work out the distance taking into account the altitude gain */
            double distanceAcrossGlobal = bounce.getDistance();
            double reflectionHeight = bounce.getHeight() / 1000.0;
            double lengthOfLine = Math.sqrt((distanceAcrossGlobal * distanceAcrossGlobal) + (reflectionHeight * reflectionHeight));
            double distanceInMetres = lengthOfLine * 1000.0;

            /* Add 'up' bounce */
            GlobalCoordinates apex = calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84, previous, azimuth, distanceInMetres / 2.0);
            hfLine.addToCoordinates(apex.getLongitude(), apex.getLatitude(), bounce.getHeight());

            /* Recalculate Azimuth between Apex and End Point */
            GeodeticCurve curve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, apex, end);
            azimuth = curve.getAzimuth();

            /* Handle the last return by working backwards from the end point, so we don't lose accuracy */
            if  (i == bounces.size() - 1) {
                hfLine.addToCoordinates(end.getLongitude(), end.getLatitude(), 0);
            } else {
                GlobalCoordinates rtn = calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84, apex, azimuth, distanceInMetres / 2.0);
                hfLine.addToCoordinates(rtn.getLongitude(), rtn.getLatitude(), 0);
                /* Recalculate Azimuth between Apex and End Point */
                curve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, rtn, end);
                azimuth = curve.getAzimuth();
                previous = rtn;
            }
        }
        hfLine.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
        hfLine.setExtrude(false);
    }
}
