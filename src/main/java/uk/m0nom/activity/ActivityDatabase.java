package uk.m0nom.activity;

import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ActivityDatabase {
    private final ActivityType type;
    private final Map<String, Activity> database;
    private final boolean specialEventActivity;

    public ActivityDatabase(ActivityType type, Map<String, Activity> database) {
        this.type = type;
        this.database = database;
        this.specialEventActivity = false;
    }

    public ActivityDatabase(ActivityType type, Map<String, Activity> database, boolean specialEventActivity) {
        this.type = type;
        this.database = database;
        this.specialEventActivity = specialEventActivity;
    }

    public ActivityType getType() {
        return type;
    }

    public Activity get(String ref) {
        return database.get(ref);
    }

    public boolean isSpecialEventActivity() {
        return specialEventActivity;
    }

    public Collection<Activity> getValues() {
        return database.values();
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
