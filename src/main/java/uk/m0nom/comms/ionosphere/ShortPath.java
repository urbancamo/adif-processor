package uk.m0nom.comms.ionosphere;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkGenerator;
import uk.m0nom.comms.CommsLinkResult;
import uk.m0nom.coords.GlobalCoords3D;

public class ShortPath implements CommsLinkGenerator {

    @Override
    public CommsLinkResult getCommunicationsLink(TransformControl control,
                                                 GlobalCoords3D start, GlobalCoords3D end,
                                                 Adif3Record rec)
    {
        return IonosphereUtils.getShortestPath(control, start, end, rec);
    }
}
