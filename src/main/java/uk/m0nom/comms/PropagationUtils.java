package uk.m0nom.comms;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.coords.GlobalCoords3D;

public class PropagationUtils {
    public static CommsLinkResult calculateGeodeticCurve(GlobalCoords3D start,
                                                         GlobalCoords3D end) {
        CommsLinkResult result = new CommsLinkResult(start, end);
        GeodeticCurve curve = new GeodeticCalculator().calculateGeodeticCurve(Ellipsoid.WGS84, start, end);
        result.setDistanceInKm(curve.getEllipsoidalDistance() / 1000);
        result.setAzimuth(curve.getAzimuth());
        return result;
    }
}
