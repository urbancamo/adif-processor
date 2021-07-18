package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.hema.HemaSummitInfo;
import uk.m0nom.adif3.contacts.Station;

public class KmlStationHemaInfo extends KmlStationActivityInfo {
    @Override
    public String getInfo(Station station) {
        return appendHemaInfo(station.getActivity(ActivityType.HEMA));
    }

    private String appendHemaInfo(Activity activity) {
        StringBuilder sb = new StringBuilder();
        HemaSummitInfo summitInfo = (HemaSummitInfo) activity;
        sb.append(String.format("HEMA: <a href=\"http://hema.org.uk/fullSummit.jsp?summitKey=%d\">%s</a><br/>",
                summitInfo.getKey(), summitInfo.getRef()));
        sb.append(String.format("%s<br/><br/>", summitInfo.getName()));
        return sb.toString();
    }
}
