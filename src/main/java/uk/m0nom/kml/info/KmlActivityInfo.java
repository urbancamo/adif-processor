package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.adif3.contacts.Station;

public abstract class KmlActivityInfo {
    public abstract String getInfo(Activity activity);
}
