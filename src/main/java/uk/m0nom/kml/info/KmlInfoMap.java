package uk.m0nom.kml.info;

import uk.m0nom.activity.ActivityType;

import java.util.HashMap;
import java.util.Map;

public class KmlInfoMap {
    private Map<ActivityType, KmlActivityInfo> infoMap;


    public KmlInfoMap() {
        infoMap = new HashMap<>();
        infoMap.put(ActivityType.SOTA, new KmlSotaInfo());
        infoMap.put(ActivityType.WOTA, new KmlWotaInfo());
        infoMap.put(ActivityType.POTA, new KmlPotaInfo());
        infoMap.put(ActivityType.HEMA, new KmlHemaInfo());
        infoMap.put(ActivityType.WWFF, new KmlWwffInfo());
    }

    public KmlActivityInfo get(ActivityType activityType) {
        return infoMap.get(activityType);
    }

}