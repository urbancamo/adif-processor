package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.sota.SotaSummitInfo;

public class KmlSotaInfo extends KmlActivityInfo {

    public String getInfo(Activity activity) {

        SotaSummitInfo summitInfo = (SotaSummitInfo) activity;
        String sb = String.format("SOTA: <a href=\"%s\">%s</a><br/>", activity.getUrl(), activity.getRef()) +
                String.format("%s<br/>", summitInfo.getName()) +
                String.format("%.0f metres, %d points<br/><br/>", summitInfo.getAltitude(), summitInfo.getPoints());
        return sb;
    }
}
