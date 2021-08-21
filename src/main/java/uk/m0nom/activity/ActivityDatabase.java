package uk.m0nom.activity;

import lombok.val;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.activity.hema.HemaSummitInfo;

import java.util.*;

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

    public Collection<Activity> findActivitiesInRadius(Activity activity, double radius) {
        List<Activity> matches = new ArrayList<>(10);

        if (activity.hasCoords()) {
            for (Activity toCheck : database.values()) {
                if (toCheck.hasCoords()) {
                    GlobalCoordinates to = toCheck.getCoords();
                    GeodeticCalculator calculator = new GeodeticCalculator();
                    GeodeticCurve curve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, activity.getCoords(), to);
                    double distance = curve.getEllipsoidalDistance();

                    if (distance < radius && !activity.equals(toCheck)) {
                        matches.add(toCheck);
                    }
                }
            }
        }
        return matches;
    }

    public int size() { return database.size(); }
}
