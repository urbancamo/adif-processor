package uk.m0nom.comms;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;
import uk.m0nom.geodesic.GeodesicUtils;
import uk.m0nom.satellite.ApSatellite;
import uk.m0nom.satellite.ApSatellites;

import java.util.List;

public class SatellitePropagation implements CommsLinkGenerator {
    private final ApSatellites apSatellites;

    public SatellitePropagation() {
        apSatellites = new ApSatellites();
    }

    @Override
    public CommsLinkResult getCommunicationsLink(TransformControl control, GlobalCoordinates start, GlobalCoordinates end,
                                                 Adif3Record rec, double myAltitude, double theirAltitude) {
        CommsLinkResult result = new CommsLinkResult();

        if (rec.getSatName() != null) {
            ApSatellite apSatellite = apSatellites.getSatellite(rec.getSatName());
            if (apSatellite == null) {
                result.setError(String.format("Unknown satellite: %s", rec.getSatName()));
                return result;
            }
            apSatellite.updateAdifRec(control, rec);
            GlobalCoordinatesWithSourceAccuracy groundStation = new GlobalCoordinatesWithSourceAccuracy(rec.getMyCoordinates(), myAltitude);
            GlobalCoordinatesWithSourceAccuracy satelliteLocation = apSatellite.getPosition(groundStation, rec.getQsoDate(), rec.getTimeOn());
            result.setSatellitePosition(satelliteLocation);

            GeodeticCalculator calculator = new GeodeticCalculator();

            GeodeticCurve betweenStationsCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, start, end);
            GeodeticCurve fromSatelliteCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, start, satelliteLocation);
            GeodeticCurve toSatelliteCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, satelliteLocation, end);
            double distance = betweenStationsCurve.getEllipsoidalDistance();

            double distanceInKm = distance / 1000;
            result.setDistanceInKm(distanceInKm);

            List<GlobalCoordinatesWithSourceAccuracy> path = result.getPath();
            path.add(new GlobalCoordinatesWithSourceAccuracy(start.getLatitude(), start.getLongitude(), myAltitude));
            path.add(satelliteLocation);
            path.add(new GlobalCoordinatesWithSourceAccuracy(end.getLatitude(), end.getLongitude(), theirAltitude));

            result.setSkyDistance(satelliteLocation.getAltitude() * 2 / 1000);

            result.setPropagation(Propagation.SATELLITE);
            result.setAltitude(satelliteLocation.getAltitude());

            // In order to complete this right angle calculation we need to shorten the geodesic distance
            // so that it is a straight line through the earth between the two points, not across the surface
            double fromDistance = GeodesicUtils.geodesicDistanceToStraightLineDistance(fromSatelliteCurve.getEllipsoidalDistance());
            double fromAngle = Math.tanh(satelliteLocation.getAltitude() / fromDistance);
            result.setFromAngle(90-Math.toDegrees(fromAngle));

            double toDistance = GeodesicUtils.geodesicDistanceToStraightLineDistance(toSatelliteCurve.getEllipsoidalDistance());
            double toAngle = Math.tanh(satelliteLocation.getAltitude() / toDistance);
            result.setToAngle(90-Math.toDegrees(toAngle));

            result.setBounces(1);
        }
        return result;
    }
}
