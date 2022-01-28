package uk.m0nom.comms.ionosphere;

import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkGenerator;
import uk.m0nom.comms.CommsLinkResult;

public class ShortPath implements CommsLinkGenerator {

    @Override
    public CommsLinkResult getCommunicationsLink(TransformControl control,
                                                  GlobalCoordinates startGc, GlobalCoordinates endGc,
                                                  Adif3Record rec, double myAltitude, double theirAltitude)
    {
        return IonosphereUtils.getShortestPath(control, startGc, endGc, rec, myAltitude, theirAltitude);
    }
}
