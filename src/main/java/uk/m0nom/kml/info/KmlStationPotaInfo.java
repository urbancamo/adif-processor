package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.pota.PotaInfo;
import uk.m0nom.adif3.contacts.Station;

public class KmlStationPotaInfo extends KmlStationActivityInfo {
    @Override
    public String getInfo(Station station) {
        return appendPotaInfo(station.getActivity(ActivityType.POTA));
    }

    private String appendPotaInfo(Activity activity) {
        PotaInfo parkInfo = (PotaInfo) activity;
        String sb = String.format("POTA: <a href=\"https://pota.app/#/park/%s\">%s</a><br/>", parkInfo.getRef(), parkInfo.getRef()) +
                String.format("%s<br/><br/>", parkInfo.getName());
        return sb;
    }
}
