package uk.m0nom.comms.ionosphere;

import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkGenerator;
import uk.m0nom.comms.CommsLinkResult;
import uk.m0nom.comms.PropagationUtils;
import uk.m0nom.coords.GlobalCoords3D;

public class LongPath implements CommsLinkGenerator {

    public CommsLinkResult getCommunicationsLink(TransformControl control,
                                                 GlobalCoords3D start, GlobalCoords3D end,
                                                 Adif3Record rec) {
        GeodeticCalculator calculator = new GeodeticCalculator();
        // Step 1 - get the shortest path curve
        GeodeticCurve shortestPath = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, start, end);

        double shortestPathDistance = shortestPath.getEllipsoidalDistance();
        double reverseAzimuth = shortestPath.getReverseAzimuth();

        // We are going to iteratively work out the long path distance now.
        double endBearing[] = new double[1];

        GlobalCoordinates inter = null;
        //while (calculator.calculateEndingGlobalCoordinates(Ellipsoid.WGS84, start, reverseAzimuth, shortestPathDistance, endBearing)) {
            //TODO
        //}
        CommsLinkResult result = null;
        return result;
    }
}
