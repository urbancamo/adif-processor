package uk.m0nom.activity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.coords.GlobalCoordinatesWithLocationSource;

@Getter
@Setter
public abstract class Activity {
    private ActivityType type;
    private String name;
    private String ref;
    private GlobalCoordinatesWithLocationSource coords;
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
}
