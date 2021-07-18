package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.sota.SotaSummitInfo;
import uk.m0nom.adif3.contacts.Station;

import java.util.logging.Logger;

public class KmlStationSotaInfo extends KmlStationActivityInfo {

    public String getInfo(Station station) {
        return appendSotaInfo(station.getActivity(ActivityType.SOTA));
    }

    private String appendSotaInfo(Activity activity) {
        SotaSummitInfo summitInfo = (SotaSummitInfo) activity;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("SOTA: <a href=\"https://summits.sota.org.uk/summit/%s\">%s</a><br/>", activity.getRef(), activity.getRef()));
        sb.append(String.format("%s<br/>", summitInfo.getName()));
        sb.append(String.format("%.0f metres, %d points<br/><br/>", summitInfo.getAltitude(), summitInfo.getPoints()));
        return sb.toString();
    }
}
