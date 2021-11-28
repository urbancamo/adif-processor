package uk.m0nom.comms;

import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.geodesic.GeodesicUtils;

import java.util.List;

public class TroposphericDuctingPropagation implements CommsLinkGenerator {

    @Override
    public CommsLinkResult getCommunicationsLink(TransformControl control, LineString hfLine,
                                                 GlobalCoordinates start, GlobalCoordinates end,
                                                 Adif3Record rec, double myAltitude, double theirAltitude) {
        /* assume daytime propagation if we don't have a QSO time */
        CommsLinkResult result = PropagationUtils.calculateGeodeticCurve(start, end);

        double avgAltitude = 0.0;
        double avgAngle = 0.0;
        double avgBase = 0.0;
        Propagation mode = null;
        List<PropagationBounce> bounces = new Troposphere().getBounces(result.getDistance());
        double skyDistance = GeodesicUtils.addBouncesToLineString(hfLine, bounces, start, end, result.getAzimuth());
        result.setSkyDistance(skyDistance);

        for (PropagationBounce bounce : bounces) {
            avgAltitude += bounce.getHeight();
            avgAngle += bounce.getAngle();
            avgBase += bounce.getBase();
            mode = bounce.getMode();
        }
        result.setPropagation(mode);
        result.setAltitude(avgAltitude / bounces.size());
        result.setBase(avgBase / bounces.size());
        result.setFromAngle(avgAngle / bounces.size());
        result.setBounces(bounces.size());

        return result;
    }
}
