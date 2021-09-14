package uk.m0nom.comms;

import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.args.TransformControl;

import java.time.LocalTime;

public interface CommsLinkGenerator {
    CommsLinkResult getCommsLink(TransformControl control, LineString hfLine, GlobalCoordinates startGc, GlobalCoordinates endGc,
                                 Adif3Record rec, double myAltitude, double theirAltitude);
}
