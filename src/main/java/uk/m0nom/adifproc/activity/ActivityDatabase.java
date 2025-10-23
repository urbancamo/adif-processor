package uk.m0nom.adifproc.activity;

import lombok.Getter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class groups all locations for an activity in a Map that can be searched using the primary reference
 * It also contains a method to obtain all activities within a given radius of a location
 */
public class ActivityDatabase {
    @Getter
    private final ActivityType type;
    private final Map<String, Activity> database;
    @Getter
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

    public Activity get(String ref) {
        return database.get(ref);
    }

    public Activity get(String ref, ZonedDateTime onDate) {
        Activity activity = database.get(ref);
        if (activity.isValid(onDate)) {
            return activity;
        }
        return null;
    }

    public Collection<Activity> getValues() {
        return database.values();
    }

    /**
     * Search for all activities that are within the given radius
     * @param centre centre activity reference to search from
     * @param radius radius in metres to search against
     * @param onDate date for which activity locations are valid
     * @return collection of activities in the given radius
     */
    public Collection<Activity> findActivitiesInRadius(Activity centre, double radius, ZonedDateTime onDate) {

        if (centre.hasCoords()) {
            return database
                    .values()
                    .parallelStream()
                    .filter(Activity::hasCoords)
                    .filter(match -> match.isValid(onDate)) // only return valid activities
                    .filter(match -> match.inRadius(centre, radius * 1000.0))
                    .toList();
        }
        return new ArrayList<>();
    }

    public int size() { return database.size(); }
}
