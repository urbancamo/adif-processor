package uk.m0nom.comms;

import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.control.TransformControl;

public class CommsVisualizer implements CommsLinkGenerator {
    public CommsLinkResult getCommsLink(TransformControl control, LineString commsLine, GlobalCoordinates startGc, GlobalCoordinates endGc,
                                        Adif3Record rec,
                                        double myAltitude, double theirAltitude) {
        CommsLinkResult result = null;

        // See if the propagation mode used is defined in the record
        if (rec.getPropMode() != null) {
            // We honour satellite mode here
            switch (rec.getPropMode()) {
                case SATELLITE:
                    result = new SatellitePropagation().getCommsLink(control, commsLine, startGc, endGc, rec, myAltitude, theirAltitude);
                    break;
                case TROPOSPHERIC_DUCTING:
                        result = new TroposphericDuctingPropagation().getCommsLink(control, commsLine, startGc, endGc, rec, myAltitude, theirAltitude);
                        break;
                default:
            }
        } else {
            result = new IonosphericPropagation().getCommsLink(control, commsLine, startGc, endGc, rec, myAltitude, theirAltitude);
        }
        return result;
    }
}