package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.wota.WotaSummitInfo;

public class KmlWotaInfo extends KmlActivityInfo {
    @Override
    public String getInfo(Activity activity) {
        WotaSummitInfo summitInfo = (WotaSummitInfo) activity;
        String sb = String.format("WOTA: <a href=\"%s\">%s</a><br/>", summitInfo.getUrl(), summitInfo.getRef()) +
                String.format("%s<br/><br/>", summitInfo.getName());
        return sb;
    }
}
