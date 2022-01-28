package uk.m0nom.comms;

import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.AntPath;
import uk.m0nom.adif3.control.TransformControl;

public interface CommsLinkGenerator {
    CommsLinkResult getCommunicationsLink(TransformControl control, GlobalCoordinates startGc, GlobalCoordinates endGc,
                                          Adif3Record rec, double myAltitude, double theirAltitude);
}
