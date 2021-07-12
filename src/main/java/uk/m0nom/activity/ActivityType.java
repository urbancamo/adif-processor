package uk.m0nom.activity;

import lombok.Getter;

@Getter
public enum ActivityType {
    WOTA("WOTA", "Wainwrights on the Air"),
    POTA("POTA", "Parks on the Air"),
    SOTA("SOTA", "Summits on the Air"),
    HEMA("HEMA", "Humps on the Air");

    private final String activityName;
    private final String activityDescription;

    ActivityType(String name, String description) {
        this.activityName = name;
        this.activityDescription = description;
    }
}
