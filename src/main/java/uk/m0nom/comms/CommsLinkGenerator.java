package uk.m0nom.comms;

import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.AntPath;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.coords.GlobalCoords3D;

public interface CommsLinkGenerator {
    CommsLinkResult getCommunicationsLink(TransformControl control, GlobalCoords3D start, GlobalCoords3D end,
                                          Adif3Record rec);
}
