package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.wwff.WwffInfo;
import uk.m0nom.adif3.contacts.Station;

public class KmlWwffInfo extends KmlActivityInfo {
    @Override
    public String getInfo(Activity activity) {
        WwffInfo wwffInfo = (WwffInfo) activity;

        String sb = String.format("WWFF: %s<br/>", wwffInfo.getRef()) +
                String.format("%s<br/><br/>", wwffInfo.getName());
        return sb;
    }
}
