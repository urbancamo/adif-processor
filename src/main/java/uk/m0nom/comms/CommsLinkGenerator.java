package uk.m0nom.comms;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.coords.GlobalCoords3D;

public interface CommsLinkGenerator {
    CommsLinkResult getCommunicationsLink(TransformControl control, GlobalCoords3D start, GlobalCoords3D end,
                                          Adif3Record rec);
}
