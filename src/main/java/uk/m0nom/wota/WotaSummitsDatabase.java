package uk.m0nom.wota;

import java.util.HashMap;
import java.util.Map;

public class WotaSummitsDatabase {
    private Map<String, WotaSummitInfo> summits;
    private Map<String, WotaSummitInfo> summitsWithSotaKey;
    private Map<String, WotaSummitInfo> summitsWithHemaKey;

    public WotaSummitsDatabase(Map<String, WotaSummitInfo> summits) {
        this.summits = summits;
        summitsWithSotaKey = new HashMap<>();
        summitsWithHemaKey = new HashMap<>();

        // Now generate the cross-reference tables
        for (WotaSummitInfo info : summits.values()) {
            if (info.sotaId != null) {
                summitsWithSotaKey.put(info.sotaId, info);
            }
            if (info.hemaId != null) {
                summitsWithHemaKey.put(info.hemaId, info);
            }
        }
    }

    public WotaSummitInfo get(String wotaId) {
        return summits.get(wotaId);
    }

    public WotaSummitInfo getFromSotaId(String sotaId) {
        return summitsWithSotaKey.get(sotaId);
    }

    public WotaSummitInfo getFromHemaId(String hemaId) {
        return summitsWithHemaKey.get(hemaId);
    }

    public int size() { return summits.size(); }
}
