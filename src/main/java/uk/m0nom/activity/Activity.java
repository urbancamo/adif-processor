package uk.m0nom.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.coords.GlobalCoords3D;

/**
 * An activity is any amateur radio activity programme or awards programme that you can participate in.
 * This class captures the typical common data set for an activity.
 */
@Getter
@Setter
@NoArgsConstructor
public abstract class Activity implements Comparable<Activity> {
    private ActivityType type;
    private String name;
    private String ref;
    private GlobalCoords3D coords;
    private String grid;
    private Double altitude;

    public Activity(ActivityType type) {
        this.type = type;
    }

    public boolean hasCoords() {
        return coords != null;
    }

    public boolean hasAltitude() { return altitude != null; }

    public boolean hasGrid() {
        return StringUtils.isNotEmpty(grid);
    }

    public abstract String getUrl();

    @Override
    public boolean equals(Object other) {
        boolean rtn = false;
        if (other instanceof Activity) {
            Activity otherActivity = (Activity) other;
            rtn = otherActivity.getRef().equals(ref);
        }
        return rtn;
    }

    @Override
    public int compareTo(Activity other) {
        String ref = getRef() != null ? getRef() : "";
        String otherRef = other.getRef() != null ? other.getRef() : "";
        return ref.compareTo(otherRef);
    }
}
