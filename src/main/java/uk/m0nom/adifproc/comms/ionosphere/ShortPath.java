package uk.m0nom.adifproc.comms.ionosphere;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.comms.CommsLinkGenerator;
import uk.m0nom.adifproc.comms.CommsLinkResult;
import uk.m0nom.adifproc.coords.GlobalCoords3D;

public class ShortPath implements CommsLinkGenerator {

    @Override
    public CommsLinkResult getCommunicationsLink(TransformControl control,
                                                 GlobalCoords3D start, GlobalCoords3D end,
                                                 Adif3Record rec)
    {
        return IonosphereUtils.getShortestPath(control, start, end, rec);
    }
}
