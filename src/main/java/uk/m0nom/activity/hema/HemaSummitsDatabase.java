package uk.m0nom.activity.hema;

import java.util.Map;

public class HemaSummitsDatabase {
    private Map<String, HemaSummitInfo> summits;

    public HemaSummitsDatabase(Map<String, HemaSummitInfo> summits) {
        this.summits = summits;
    }

    public HemaSummitInfo get(String summitCode) {
        return summits.get(summitCode);
    }

    public int size() { return summits.size(); }
}
