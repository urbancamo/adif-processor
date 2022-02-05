package uk.m0nom.comms.ionosphere;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkGenerator;
import uk.m0nom.comms.CommsLinkResult;
import uk.m0nom.coords.GlobalCoords3D;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Long Path HF Propagation Visualization.
 * We do a short path computation, take the 180 degree angle and then fire off the signal in that direction, with the
 * same hop distance until we get close to the target station.
 * Then we add a hop to cover the difference and recalculate.
 * This is the longest path long path.
 */
public class LongPath implements CommsLinkGenerator {
    private static final Logger logger = Logger.getLogger(LongPath.class.getName());

    public CommsLinkResult getCommunicationsLink(TransformControl control,
                                                 GlobalCoords3D start, GlobalCoords3D end,
                                                 Adif3Record rec)
    {
        CommsLinkResult shortPathResult = IonosphereUtils.getShortestPath(control, start, end, rec);

        GeodeticCalculator calculator = new GeodeticCalculator();
        // Step 1 - get the shortest path curve
        GeodeticCurve shortestPath = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, start, end);

        double longPathBearing = shortestPath.getAzimuth() + 180.0;

        // The short path bounce distance across the earth is used as a step size for determining the long path
        double bounceDistance = shortPathResult.getApexes().get(0).getDistanceAcrossEarth() * 1000.0;

        double endBearing[] = new double[1];

        List<GlobalCoordinates> steps = new ArrayList<>();

        // Now we're going to use the short path bounce distance to determine the number of bounces via long path
        // this won't be exact, so we'll need to then round up the number of bounces then divide by the total
        // distance accordingly.
        double distanceToTargetStation = Double.MAX_VALUE;
        double currentBearing = longPathBearing;

        GlobalCoordinates stepLocation = start;
        while (distanceToTargetStation >= bounceDistance) {
            stepLocation = calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84, stepLocation, currentBearing, bounceDistance, endBearing);
            steps.add(stepLocation);

            // Use the short path calculation to determine distance from current step position to target station.
            // To start with the distance will increase, but eventually this will start closing
            GeodeticCurve shortestPathFromStepToTargetStation = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, stepLocation, end);
            distanceToTargetStation = shortestPathFromStepToTargetStation.getEllipsoidalDistance();

            currentBearing = endBearing[0];
            if (currentBearing < 0) {
                currentBearing = 360 + currentBearing;
            }
            logger.info(String.format("%d: bearing: %.3f, distanceToTargetStation: %.0f", steps.size(), currentBearing, distanceToTargetStation));
        }
        // This is the distance remaining, which is less than the bounce distance
        double delta = bounceDistance - distanceToTargetStation;
        logger.info(String.format("last distanceToTargetStation: %.0f, bounceDistance: %.0f, delta: %.0f",
                distanceToTargetStation, bounceDistance, delta));

        // Round up the number of bounces then
        double totalDistance = (bounceDistance * steps.size()) + delta;
        double longPathBounceDistance = totalDistance / (steps.size() + 1);

        // Now we do it all again with the new bounce distance.
        CommsLinkResult result = new CommsLinkResult();
        double reflectionAltitude = shortPathResult.getAltitude();
        result.setAltitude(reflectionAltitude);

        result.setAzimuth(longPathBearing);
        result.setDistanceInKm(totalDistance / 1000.0);

        int bounceCount = steps.size() + 1;
        result.setBounces(bounceCount);
        result.setPropagation(shortPathResult.getPropagation());
        result.setStart(start);
        result.setEnd(end);

        // We now have a long path bounce distance, so we can calculate the new angle and sky distance from this
        // knowing the reflection height
        bounceDistance = longPathBounceDistance / 2.0;

        // Apex calculator works in km, adjust parameters accordingly
        double takeoffAngle = IonosphericApexCalculator.calculateTakeoffAngleFromDistanceAcrossEarth(bounceDistance / 1000.0, reflectionAltitude / 1000.0);
        result.setFromAngle(takeoffAngle);
        // Determine sky distance
        double skyDistance = IonosphericApexCalculator.calculateDistanceToIonosphere(reflectionAltitude, takeoffAngle);
        result.setSkyDistance(skyDistance * bounceCount / 1000.0);

        List<GlobalCoords3D> path = new ArrayList<>(result.getBounces());
        currentBearing = longPathBearing;
        path.add(new GlobalCoords3D(start.getLatitude(), start.getLongitude(), 0.0));
        stepLocation = start;

        for (int i = 1; i <= bounceCount; i++) {
            stepLocation = calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84, stepLocation, currentBearing, bounceDistance, endBearing);
            path.add(new GlobalCoords3D(stepLocation.getLatitude(), stepLocation.getLongitude(), reflectionAltitude));
            currentBearing = endBearing[0];
            if (currentBearing < 0) {
                currentBearing = 360 + currentBearing;
            }

            if (i < bounceCount) {
                stepLocation = calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84, stepLocation, currentBearing, bounceDistance, endBearing);
                path.add(new GlobalCoords3D(stepLocation.getLatitude(), stepLocation.getLongitude(), 0.0));
            }

            currentBearing = endBearing[0];
            if (currentBearing < 0) {
                currentBearing = 360 + currentBearing;
            }
        }
        path.add(new GlobalCoords3D(end.getLatitude(), end.getLongitude(), 0.0));
        result.setPath(path);
        return result;
    }
}
