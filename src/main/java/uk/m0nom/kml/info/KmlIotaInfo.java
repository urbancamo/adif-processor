package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.iota.IotaInfo;

public class KmlIotaInfo extends KmlActivityInfo {

    public String getInfo(Activity activity) {

        IotaInfo iotaInfo = (IotaInfo) activity;
        return String.format("IOTA: <a href=\"%s\">%s</a><br/>", activity.getUrl(), activity.getRef()) +
                String.format("%s<br/>", iotaInfo.getName());
    }
}
