package uk.m0nom.comms;

import de.micromata.opengis.kml.v_2_2_0.AltitudeMode;
import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.geodesic.GeodesicUtils;
import uk.m0nom.satellite.Satellite;
import uk.m0nom.satellite.SatellitePosition;
import uk.m0nom.satellite.Satellites;

public class SatellitePropagation implements CommsLinkGenerator {
    private Satellites satellites;

    public SatellitePropagation() {
        satellites = new Satellites();
    }

    @Override
    public CommsLinkResult getCommsLink(TransformControl control, LineString hfLine, GlobalCoordinates start, GlobalCoordinates end,
                                        Adif3Record rec, double myAltitude, double theirAltitude) {
        CommsLinkResult result = new CommsLinkResult();

        if (rec.getSatName() != null) {
            Satellite satellite = satellites.getSatellite(rec.getSatName());
            SatellitePosition satelliteLocation = satellite.getPosition(rec.getTimeOn());

            // Calculate ground distance between two stations
            GeodeticCalculator calculator = new GeodeticCalculator();

            GeodeticCurve betweenStationsCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, start, end);
            GeodeticCurve fromSatelliteCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, start, satelliteLocation.getPosition());
            GeodeticCurve toSatelliteCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, satelliteLocation.getPosition(), end);
            double distance = betweenStationsCurve.getEllipsoidalDistance();

            double distanceInKm = distance / 1000;
            result.setDistance(distanceInKm);

            hfLine.addToCoordinates(start.getLongitude(), start.getLatitude(), myAltitude);
            hfLine.addToCoordinates(satelliteLocation.getPosition().getLongitude(), satelliteLocation.getPosition().getLatitude(), satelliteLocation.getAltitude());
            hfLine.addToCoordinates(end.getLongitude(), end.getLatitude(), theirAltitude);

            hfLine.setAltitudeMode(AltitudeMode.RELATIVE_TO_GROUND);
            hfLine.setExtrude(false);

            result.setSkyDistance(satelliteLocation.getAltitude() * 2 / 1000);

            result.setMode(Propagation.SATELLITE);
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
