package uk.m0nom.adifproc.comms;

import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.comms.ionosphere.IonosphereUtils;
import uk.m0nom.adifproc.coords.GlobalCoords3D;

public class InternetPropagation implements CommsLinkGenerator {
    @Override
    public CommsLinkResult getCommunicationsLink(TransformControl control,
                                                 GlobalCoords3D start, GlobalCoords3D end,
                                                 Adif3Record rec) {
        CommsLinkResult result = IonosphereUtils.getShortestPath(control, start, end, rec);

        result.setPropagation(Propagation.INTERNET);
        result.setSkyDistance(0);
        result.setAltitude(0.0);
        result.setBase(0);
        result.setBounces(0);

        return result;
    }
}
