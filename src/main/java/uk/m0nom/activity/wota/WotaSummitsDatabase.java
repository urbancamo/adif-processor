package uk.m0nom.activity.wota;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityType;

import java.util.HashMap;
import java.util.Map;

/**
 * We extend the standard Activity Database here to store two Maps to cross-reference
 * WOTA summits with HEMA and SOTA summits. Where a WOTA coincides with one of these summits
 * we record all activity references. In the case of WOTA and SOTA both references can also be
 * retained in the ADIF output file as SOTA has its own field SOTA_REF
 *
 */
public class WotaSummitsDatabase extends ActivityDatabase {
    private final Map<String, Activity> summitsWithSotaKey;
    private final Map<String, Activity> summitsWithHemaKey;

    public WotaSummitsDatabase(ActivityType type, Map<String, Activity> summits) {
        super(type, summits);

        summitsWithSotaKey = new HashMap<>();
        summitsWithHemaKey = new HashMap<>();

        // Now generate the cross-reference tables
        for (Activity activity : summits.values()) {
            if (activity instanceof WotaInfo) {
                WotaInfo info = (WotaInfo) activity;
                if (info.getSotaId() != null) {
                    summitsWithSotaKey.put(info.getSotaId(), info);
                }
                if (info.getHemaId() != null) {
                    summitsWithHemaKey.put(info.getHemaId(), info);
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
