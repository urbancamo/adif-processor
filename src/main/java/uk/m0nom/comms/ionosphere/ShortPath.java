package uk.m0nom.comms.ionosphere;

import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.*;
import uk.m0nom.geodesic.GeodesicUtils;

import java.time.LocalTime;
import java.util.List;

public class ShortPath implements CommsLinkGenerator {

    @Override
    public CommsLinkResult getCommunicationsLink(TransformControl control, LineString hfLine,
                                                  GlobalCoordinates startGc, GlobalCoordinates endGc,
                                                  Adif3Record rec, double myAltitude, double theirAltitude)
    {
        return IonosphereUtils.getShortestPath(control, hfLine, startGc, endGc, rec, myAltitude, theirAltitude);
    }
}
