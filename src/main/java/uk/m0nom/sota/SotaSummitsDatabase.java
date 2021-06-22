package uk.m0nom.sota;

import java.util.Map;

public class SotaSummitsDatabase {
    private Map<String, SotaSummitInfo> summits;

    public SotaSummitsDatabase(Map<String, SotaSummitInfo> summits) {
        this.summits = summits;
    }

    public SotaSummitInfo get(String summitCode) {
        return summits.get(summitCode);
    }

    public int size() { return summits.size(); }
}
