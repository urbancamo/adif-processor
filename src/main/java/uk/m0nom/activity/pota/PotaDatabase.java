package uk.m0nom.activity.pota;

import java.util.Map;

public class PotaDatabase {
    private Map<String, PotaInfo> parks;

    public PotaDatabase(Map<String, PotaInfo> parks) {
        this.parks = parks;
    }

    public PotaInfo get(String summitCode) {
        return parks.get(summitCode);
    }

    public int size() { return parks.size(); }
}
