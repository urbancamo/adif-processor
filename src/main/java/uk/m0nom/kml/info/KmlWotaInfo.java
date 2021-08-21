package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.wota.WotaSummitInfo;
import uk.m0nom.adif3.contacts.Station;

public class KmlWotaInfo extends KmlActivityInfo {
    @Override
    public String getInfo(Activity activity) {
        StringBuilder sb = new StringBuilder();
        WotaSummitInfo summitInfo = (WotaSummitInfo) activity;
        sb.append(String.format("WOTA: <a href=\"%s\">%s</a><br/>", summitInfo.getUrl(), summitInfo.getRef()));
        sb.append(String.format("%s<br/><br/>", summitInfo.getName()));
        return sb.toString();
    }
}
