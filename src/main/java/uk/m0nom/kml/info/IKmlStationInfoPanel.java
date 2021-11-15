package uk.m0nom.kml.info;

import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.contacts.Station;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkResult;

public interface IKmlStationInfoPanel {
    String getPanelContentForStation(TransformControl control, Station station);
}
