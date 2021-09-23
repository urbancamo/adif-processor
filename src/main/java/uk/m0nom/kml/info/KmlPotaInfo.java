package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.pota.PotaInfo;

public class KmlPotaInfo extends KmlActivityInfo {
    @Override
    public String getInfo(Activity activity) {
        PotaInfo parkInfo = (PotaInfo) activity;
        return String.format("POTA: <a href=\"%s\">%s</a><br/>", parkInfo.getUrl(), parkInfo.getRef()) +
                String.format("%s<br/><br/>", parkInfo.getName());
    }
}
