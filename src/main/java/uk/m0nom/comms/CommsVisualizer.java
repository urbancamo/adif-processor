package uk.m0nom.comms;

import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.geodesic.GeodesicUtils;

import java.time.LocalTime;

public class CommsVisualizer implements CommsLinkGenerator {
    public CommsLinkResult getCommsLink(TransformControl control, LineString commsLine, GlobalCoordinates startGc, GlobalCoordinates endGc,
                                        Adif3Record rec,
                                        double myAltitude, double theirAltitude) {
        CommsLinkResult result = null;

        // See if the propagation mode used is defined in the record
        if (rec.getPropMode() != null) {
            // We honour satellite mode here
            if (rec.getPropMode() == Propagation.SATELLITE) {
                result = new SatellitePropagation().getCommsLink(control, commsLine, startGc, endGc, rec, myAltitude, theirAltitude);
            }
        } else {
            result = new IonosphericPropagation().getCommsLink(control, commsLine, startGc, endGc, rec, myAltitude, theirAltitude);
        }
        return result;
    }
}