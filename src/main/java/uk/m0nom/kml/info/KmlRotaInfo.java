package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.rota.RotaInfo;

public class KmlRotaInfo extends KmlActivityInfo {
    @Override
    public String getInfo(Activity activity) {
        RotaInfo info = (RotaInfo) activity;
        return String.format("Railway: %s<br/>Club: %s<br/>WAB: %s<br/>",
                info.getName(), info.getClub(), info.getWab());
    }
}