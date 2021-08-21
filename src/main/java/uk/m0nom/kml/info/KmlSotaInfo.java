package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.sota.SotaSummitInfo;
import uk.m0nom.adif3.contacts.Station;

public class KmlSotaInfo extends KmlActivityInfo {

    public String getInfo(Activity activity) {

        SotaSummitInfo summitInfo = (SotaSummitInfo) activity;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("SOTA: <a href=\"%s\">%s</a><br/>", activity.getUrl(), activity.getRef()));
        sb.append(String.format("%s<br/>", summitInfo.getName()));
        sb.append(String.format("%.0f metres, %d points<br/><br/>", summitInfo.getAltitude(), summitInfo.getPoints()));
        return sb.toString();
    }
}
