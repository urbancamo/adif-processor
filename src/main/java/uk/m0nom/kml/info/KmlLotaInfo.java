package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.lota.LotaInfo;

public class KmlLotaInfo extends KmlActivityInfo {
    @Override
    public String getInfo(Activity activity) {
        LotaInfo info = (LotaInfo) activity;
        return String.format("Lighthouse Ref: %s<br/>", info.getRef()) +
                String.format("Name: %s<br/><br/>", info.getName());
    }
}
