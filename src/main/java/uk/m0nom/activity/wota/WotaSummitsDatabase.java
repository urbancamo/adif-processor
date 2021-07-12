package uk.m0nom.activity.wota;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;

import java.util.HashMap;
import java.util.Map;

public class WotaSummitsDatabase extends ActivityDatabase {
    private Map<String, Activity> summitsWithSotaKey;
    private Map<String, Activity> summitsWithHemaKey;

    public WotaSummitsDatabase(ActivityType type, Map<String, Activity> summits) {
        super(type, summits);

        summitsWithSotaKey = new HashMap<>();
        summitsWithHemaKey = new HashMap<>();

        // Now generate the cross-reference tables
        for (Activity activity : summits.values()) {
            if (activity instanceof WotaSummitInfo) {
                WotaSummitInfo info = (WotaSummitInfo) activity;
                if (info.sotaId != null) {
                    summitsWithSotaKey.put(info.sotaId, info);
                }
                if (info.hemaId != null) {
                    summitsWithHemaKey.put(info.hemaId, info);
                }
            }
        }
    }

    public Activity getFromSotaId(String ref) {
        return summitsWithSotaKey.get(ref);
    }

    public Activity getFromHemaId(String ref) {
        return summitsWithHemaKey.get(ref);
    }
}
