package uk.m0nom.kml.info;

import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.adif3.contacts.Station;

public abstract class KmlStationActivityInfo {
    public abstract String getInfo(Station station);
}
