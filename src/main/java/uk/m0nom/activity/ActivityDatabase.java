package uk.m0nom.activity;

import uk.m0nom.activity.hema.HemaSummitInfo;

import java.util.Map;

public class ActivityDatabase {
    private ActivityType type;
    private Map<String, Activity> database;

    public ActivityDatabase(ActivityType type, Map<String, Activity> database) {
        this.type = type;
        this.database = database;
    }

    public ActivityType getType() { return type; }

    public Activity get(String ref) { return database.get(ref);
    }

    public int size() { return database.size(); }
}
