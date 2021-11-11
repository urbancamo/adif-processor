package uk.m0nom.kml.info;

import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkResult;

public interface IKmlContactInfoPanel {
    String getPanelContentForCommsLink(TransformControl control, Qso qso, CommsLinkResult result);
}
