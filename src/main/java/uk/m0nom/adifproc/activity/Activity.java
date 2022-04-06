package uk.m0nom.adifproc.activity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import uk.m0nom.adifproc.coords.GlobalCoords3D;

import java.time.LocalDate;

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

    // Do not refactor out - used in Thymeleaf template
    public String getAltitudeInMetres() {
        return String.format("%.0f m", altitude);
    }

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

    public boolean isValid(LocalDate onDate) {
        return true;
    }
    
    public boolean inRadius(Activity other, double radius) {
        GeodeticCalculator calculator = new GeodeticCalculator();
        GeodeticCurve curve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, other.getCoords(), coords);
        return curve.getEllipsoidalDistance() < radius && !other.equals(this);
    }

}
