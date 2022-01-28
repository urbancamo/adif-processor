package uk.m0nom.comms;

import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.AntPath;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.ionosphere.LongPath;
import uk.m0nom.comms.ionosphere.ShortPath;

/**
 * Calculates the path of propagation between two points on the earth based on a propagation mode
 */
public class CommsVisualizer implements CommsLinkGenerator {
    public CommsLinkResult getCommunicationsLink(TransformControl control, GlobalCoordinates startGc, GlobalCoordinates endGc,
                                                 Adif3Record rec,
                                                 double myAltitude, double theirAltitude) {
        CommsLinkResult result = null;

        // See if the propagation mode used is defined in the record
        if (rec.getPropMode() != null) {
            // We honour satellite mode here
            switch (rec.getPropMode()) {
                case SATELLITE:
                    result = new SatellitePropagation().getCommunicationsLink(control, startGc, endGc, rec, myAltitude, theirAltitude);
                    break;
                case TROPOSPHERIC_DUCTING:
                        result = new TroposphericDuctingPropagation().getCommunicationsLink(control, startGc, endGc, rec, myAltitude, theirAltitude);
                        break;
                default:
            }
        } else {
            if (rec.getAntPath() == AntPath.LONG) {
                result = new LongPath().getCommunicationsLink(control, startGc, endGc, rec, myAltitude, theirAltitude);
            } else {
                result = new ShortPath().getCommunicationsLink(control, startGc, endGc, rec, myAltitude, theirAltitude);
            }
        }
        return result;
    }
}