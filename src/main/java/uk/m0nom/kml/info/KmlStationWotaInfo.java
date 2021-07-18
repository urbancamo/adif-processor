package uk.m0nom.kml.info;

import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.wota.WotaSummitInfo;
import uk.m0nom.adif3.contacts.Station;

public class KmlStationWotaInfo extends KmlStationActivityInfo {
    @Override
    public String getInfo(Station station) {
        StringBuilder sb = new StringBuilder();
        WotaSummitInfo summitInfo = (WotaSummitInfo) station.getActivity(ActivityType.WOTA);
        String lookupRef = summitInfo.getRef();
        if (StringUtils.equals(summitInfo.getBook(), "OF")) {
            // need to compensate for LDO weird numbering
            lookupRef = String.format("LDO-%03d", summitInfo.getInternalId());
        }
        sb.append(String.format("WOTA: <a href=\"https://wota.org.uk/MM_%s\">%s</a><br/>", lookupRef, summitInfo.getRef()));
        sb.append(String.format("%s<br/><br/>", summitInfo.getName()));
        return sb.toString();
    }
}
