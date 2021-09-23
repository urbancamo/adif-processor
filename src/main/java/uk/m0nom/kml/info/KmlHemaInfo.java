package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.hema.HemaSummitInfo;

public class KmlHemaInfo extends KmlActivityInfo {
    @Override
    public String getInfo(Activity activity) {
        HemaSummitInfo summitInfo = (HemaSummitInfo) activity;
        String sb = String.format("HEMA: <a href=\"http://hema.org.uk/fullSummit.jsp?summitKey=%d\">%s</a><br/>",
                summitInfo.getKey(), summitInfo.getRef()) +
                String.format("%s<br/><br/>", summitInfo.getName());
        return sb;
    }
}
