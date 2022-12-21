package uk.m0nom.adifproc.activity;

import lombok.Getter;

/**
 * Each of the supported activities is enumerated here with the activity name
 */
@Getter
public enum ActivityType {
    WOTA("WOTA", "Wainwrights on the Air"),
    POTA("POTA", "Parks on the Air"),
    SOTA("SOTA", "Summits on the Air"),
    HEMA("HEMA", "Humps on the Air"),
    WWFF("WWFF", "World Wide Flora & Fauna"),
    COTA("COTA", "Castles on the Air"),
    LOTA("LOTA", "Lighthouses on the Air"),
    ROTA("ROTA", "Railways on the Air"),
    IOTA("IOTA", "Islands on the Air"),
    GMA("GMA", "Global Mountain Activity");

    private final String activityName;
    private final String activityDescription;

    ActivityType(String name, String description) {
        this.activityName = name;
        this.activityDescription = description;
    }

    public static boolean hasOwnAdifField(ActivityType type) {
        return type == ActivityType.SOTA || type == ActivityType.WWFF || type == ActivityType.POTA;
    }
}
