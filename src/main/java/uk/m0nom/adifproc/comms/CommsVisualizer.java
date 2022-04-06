package uk.m0nom.adifproc.comms;

import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.AntPath;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.comms.ionosphere.LongPath;
import uk.m0nom.adifproc.comms.ionosphere.ShortPath;

/**
 * Calculates the path of propagation between two points on the earth based on a propagation mode
 */
public class CommsVisualizer implements CommsLinkGenerator {
    public CommsLinkResult getCommunicationsLink(TransformControl control, GlobalCoords3D startGc, GlobalCoords3D endGc,
                                                 Adif3Record rec) {
        CommsLinkResult result = null;

        // See if the propagation mode used is defined in the record
        if (rec.getPropMode() != null) {
            // We honour satellite mode here
            switch (rec.getPropMode()) {
                case SATELLITE:
                    result = new SatellitePropagation().getCommunicationsLink(control, startGc, endGc, rec);
                    break;
                case TROPOSPHERIC_DUCTING:
                        result = new TroposphericDuctingPropagation().getCommunicationsLink(control, startGc, endGc, rec);
                        break;
                default:
            }
        } else {
            if (rec.getAntPath() == AntPath.LONG) {
                result = new LongPath().getCommunicationsLink(control, startGc, endGc, rec);
            } else {
                result = new ShortPath().getCommunicationsLink(control, startGc, endGc, rec);
            }
        }
        return result;
    }
}