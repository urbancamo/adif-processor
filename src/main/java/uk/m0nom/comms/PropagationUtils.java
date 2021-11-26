package uk.m0nom.comms;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;

public class PropagationUtils {
    public static CommsLinkResult calculateGeodeticCurve(GlobalCoordinates start,
                                                         GlobalCoordinates end) {
        CommsLinkResult result = new CommsLinkResult();
        GeodeticCurve curve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.WGS84, start, end);
        result.setDistance(curve.getEllipsoidalDistance() / 1000);
        result.setAzimuth(curve.getAzimuth());
        return result;
    }
}
