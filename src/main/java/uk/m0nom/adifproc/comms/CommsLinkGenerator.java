package uk.m0nom.adifproc.comms;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.coords.GlobalCoords3D;

public interface CommsLinkGenerator {
    CommsLinkResult getCommunicationsLink(TransformControl control, GlobalCoords3D start, GlobalCoords3D end,
                                          Adif3Record rec);
}
