package uk.m0nom.comms.ionosphere;

import com.fasterxml.jackson.databind.annotation.JsonAppend;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkGenerator;
import uk.m0nom.comms.CommsLinkResult;
import uk.m0nom.comms.PropagationApex;
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

        double longPathBearing = IonosphereUtils.normalisedAngleAddition(shortestPath.getAzimuth(), 180.0);

        // The short path bounce distance across the earth is used as a step size for determining the long path
        //double bounceDistance = shortPathResult.getApexes().get(0).getDistanceAcrossEarth() * 1000.0;

        //double stepDistance = bounceDistance / 2.0;

        double endBearing[] = new double[1];

        int steps = 0;

        // Now we're going to use the short path bounce distance to determine the number of bounces via long path
        // this won't be exact, so we'll need to then round up the number of bounces then divide by the total
        // distance accordingly.
        double distanceToTargetStation = Double.MAX_VALUE;
        double currentBearing = longPathBearing;

        GlobalCoordinates stepLocation = start;
        // Determine the distance between start and end by incrementally calculating using the start bearing and
        // wait for convergence. We use 50km as a nominal step
        double stepDistance = 50000.00;
        while (distanceToTargetStation >= stepDistance) {
            stepLocation = calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84, stepLocation, currentBearing, stepDistance, endBearing);
            steps++;

            // Use the short path calculation to determine distance from current step position to target station.
            // To start with the distance will increase, but after a while this will close in on the end to within a single step
            GeodeticCurve shortestPathFromStepToTargetStation = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, stepLocation, end);
            distanceToTargetStation = shortestPathFromStepToTargetStation.getEllipsoidalDistance();

            currentBearing = IonosphereUtils.normaliseAngle(endBearing[0]);
            logger.info(String.format("%d: bearing: %.3f, distanceToTargetStation: %.0f", steps, currentBearing, distanceToTargetStation));
        }

        //logger.info(String.format("last distanceToTargetStation: %.0f, bounceDistance: %.0f, delta: %.0f",
        //        distanceToTargetStation, bounceDistance, delta));

        // Calculate the total distance to the target station
        double totalDistance = (stepDistance * steps) + distanceToTargetStation;

        // Use the antenna angle to calculate the maximum distance across the earth
        double takeOffAngle = control.getAntenna().getTakeOffAngle();

        PropagationApex apex = IonosphericApexCalculator.calculateDistanceOfApex(shortPathResult.getAltitude()/1000.0, takeOffAngle);
        // Now work out distance across the earth
        double longPathBounceDistance = IonosphericApexCalculator.calculateDistanceAcrossEarth(apex) * 2.0;

        int bounces = (int)(totalDistance / (longPathBounceDistance * 1000.0));
        bounces = bounces + 1;

        int stepCount = bounces * 2;
        double longPathStepDistance = totalDistance / stepCount;

        // Now we do it all again with the new bounce distance.
        CommsLinkResult result = new CommsLinkResult();
        double reflectionAltitude = shortPathResult.getAltitude();
        result.setAltitude(reflectionAltitude);

        result.setAzimuth(longPathBearing);
        result.setDistanceInKm(totalDistance / 1000.0);

        result.setBounces(stepCount);
        result.setPropagation(shortPathResult.getPropagation());
        result.setStart(start);
        result.setEnd(end);


        // We now have a long path bounce distance, so we can calculate the new angle and sky distance from this
        // knowing the reflection height

        // Apex calculator works in km, adjust parameters accordingly
        double takeoffAngle = IonosphericApexCalculator.calculateTakeoffAngleFromDistanceAcrossEarth(longPathStepDistance / 1000.0, reflectionAltitude / 1000.0);
        result.setFromAngle(takeoffAngle);
        // Determine sky distance
        double skyDistance = IonosphericApexCalculator.calculateDistanceToIonosphere(reflectionAltitude / 1000.0, takeoffAngle);
        result.setSkyDistance(skyDistance * stepCount);

        List<GlobalCoords3D> path = new ArrayList<>(result.getBounces());
        currentBearing = longPathBearing;
        path.add(new GlobalCoords3D(start.getLatitude(), start.getLongitude(), 0.0));
        stepLocation = start;

        int bounceCount = stepCount / 2;
        for (int i = 1; i <= bounceCount; i++) {

            // Up to the Ionosphere
            stepLocation = calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84, stepLocation, currentBearing, longPathStepDistance, endBearing);
            path.add(new GlobalCoords3D(stepLocation.getLatitude(), stepLocation.getLongitude(), reflectionAltitude));
            currentBearing = IonosphereUtils.normaliseAngle(endBearing[0]);

            // Back down, except on the last bounce
            if (i < bounceCount) {
                stepLocation = calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84, stepLocation, currentBearing, longPathStepDistance, endBearing);
                path.add(new GlobalCoords3D(stepLocation.getLatitude(), stepLocation.getLongitude(), 0.0));
            }

            currentBearing = endBearing[0];
            if (currentBearing < 0) {
                currentBearing = 360 + currentBearing;
            }
        }
        // Add the destination as the final location
        path.add(new GlobalCoords3D(end.getLatitude(), end.getLongitude(), 0.0));
        result.setPath(path);
        return result;
    }
}
