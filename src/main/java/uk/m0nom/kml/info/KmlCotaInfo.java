package uk.m0nom.kml.info;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.cota.CotaInfo;
import uk.m0nom.activity.pota.PotaInfo;

public class KmlCotaInfo extends KmlActivityInfo {
    @Override
    public String getInfo(Activity activity) {
        CotaInfo cotaInfo = (CotaInfo) activity;
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Castle Ref: %s</a><br/>", cotaInfo.getRef()));
        sb.append(String.format("Castle Name:<br/>%s<br/>", cotaInfo.getName()));
        if (!cotaInfo.hasCoords()) {
            sb.append("No location data available for Castle<br/>");
        }
        sb.append("<br/>");
        return sb.toString();
    }
}
