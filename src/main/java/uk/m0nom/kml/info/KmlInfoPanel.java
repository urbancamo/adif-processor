package uk.m0nom.kml.info;

import uk.m0nom.adif3.contacts.Station;

public interface KmlInfoPanel {
    String getPanelContent(Station station);
}
