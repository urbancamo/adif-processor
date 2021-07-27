package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.pota.PotaInfo;
import uk.m0nom.activity.wwff.WwffInfo;
import uk.m0nom.adif3.contacts.Station;

public class KmlStationWwffInfo extends KmlStationActivityInfo {
    @Override
    public String getInfo(Station station) {
        return appendWwffInfo(station.getActivity(ActivityType.WWFF));
    }

    private String appendWwffInfo(Activity activity) {
        WwffInfo wwffInfo = (WwffInfo) activity;

        String sb = String.format("WWFF: %s<br/>", wwffInfo.getRef()) +
                String.format("%s<br/><br/>", wwffInfo.getName());
        return sb;
    }
}
