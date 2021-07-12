package uk.m0nom.activity;

import lombok.Getter;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;

@Getter
@Setter
public abstract class Activity {
    public ActivityType type;
    public String name;
    public String ref;
    public GlobalCoordinates coords;

    public boolean hasCoords() {
        return coords != null;
    }
}
