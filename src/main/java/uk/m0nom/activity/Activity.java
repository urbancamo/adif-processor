package uk.m0nom.activity;

import lombok.Getter;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;

@Getter
@Setter
public abstract class Activity {
    private ActivityType type;
    private String name;
    private String ref;
    private GlobalCoordinates coords;

    public Activity(ActivityType type) {
        this.type = type;
    }

    public boolean hasCoords() {
        return coords != null;
    }
}
