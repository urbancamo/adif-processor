package uk.m0nom.comms;

import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.coords.GlobalCoords3D;
import uk.m0nom.geodesic.GeodesicUtils;

import java.util.List;

/**
 * Models tropospheric ducting which usual occur when there is a temperature inversion.
 * The signal gets 'trapped' in a 'duct' and travels in the duct for an extended distance
 * before being picked up by the contacted station.
 *
 * Duct heights and base altitudes vary in reality but it is difficult to predict the exact
 * ducting effect so this is an approximation.
 */
public class TroposphericDuctingPropagation implements CommsLinkGenerator {

    @Override
    public CommsLinkResult getCommunicationsLink(TransformControl control,
                                                 GlobalCoords3D start, GlobalCoords3D end,
                                                 Adif3Record rec) {
        /* assume daytime propagation if we don't have a QSO time */
        CommsLinkResult result = PropagationUtils.calculateGeodeticCurve(start, end);

        double avgAltitude = 0.0;
        double avgAngle = 0.0;
        double avgBase = 0.0;
        Propagation mode = null;
        List<PropagationApex> bounces = new Troposphere().getBounces(result.getDistanceInKm());
        double skyDistance = GeodesicUtils.calculatePath(result.getPath(), bounces, start, end, result.getAzimuth());
        result.setSkyDistance(skyDistance);

        for (PropagationApex bounce : bounces) {
            avgAltitude += bounce.getApexHeight();
            avgAngle += bounce.getRadiationAngle();
            avgBase += bounce.getBaseHeight();
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
