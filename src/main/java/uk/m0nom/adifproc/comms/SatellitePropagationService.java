package uk.m0nom.adifproc.comms;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Propagation;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.geodesic.GeodesicUtils;
import uk.m0nom.adifproc.satellite.ApSatellite;
import uk.m0nom.adifproc.satellite.ApSatelliteService;

import java.util.List;

@Service
public class SatellitePropagationService implements CommsLinkGenerator {
    private final ApSatelliteService apSatelliteService;

    public SatellitePropagationService(ApSatelliteService apSatelliteService) {
        this.apSatelliteService = apSatelliteService;
    }

    @Override
    public CommsLinkResult getCommunicationsLink(TransformControl control,
                                                 GlobalCoords3D start,
                                                 GlobalCoords3D end,
                                                 Adif3Record rec) {
        CommsLinkResult result = new CommsLinkResult();

        if (rec.getSatName() != null) {
            ApSatellite apSatellite = apSatelliteService.getSatellite(rec.getSatName(), rec.getQsoDate());
            if (apSatellite == null) {
                result.setError(String.format("Unknown satellite: %s", rec.getSatName()));
                return result;
            }
            apSatellite.updateAdifRec(control, rec);
            GlobalCoords3D satelliteLocation = apSatellite.getPosition(start, rec.getQsoDate(), rec.getTimeOn());
            result.setSatellitePosition(satelliteLocation);

            GeodeticCalculator calculator = new GeodeticCalculator();

            GeodeticCurve betweenStationsCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, start, end);
            GeodeticCurve fromSatelliteCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, start, satelliteLocation);
            GeodeticCurve toSatelliteCurve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, satelliteLocation, end);
            double distance = betweenStationsCurve.getEllipsoidalDistance();

            double distanceInKm = distance / 1000;
            result.setDistanceInKm(distanceInKm);

            List<GlobalCoords3D> path = result.getPath();
            path.add(start);
            path.add(satelliteLocation);
            path.add(end);

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
