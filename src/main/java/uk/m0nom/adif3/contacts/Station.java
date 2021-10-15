package uk.m0nom.adif3.contacts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;
import uk.m0nom.qrz.QrzCallsign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
public class Station {
    private String callsign;
    private QrzCallsign qrzInfo;
    private List<Qso> qsos;

    private Map<ActivityType, Activity> activities;
    private String grid;
    private GlobalCoordinatesWithSourceAccuracy coordinates;

    public Station() {
        activities = new HashMap<>();
        qsos = new ArrayList<>();
    }

    public Station(String callsign, Qso initialQso) {
        this();
        this.callsign = StringUtils.trim(callsign).toUpperCase();
        addQso(initialQso);
    }

    public void addQso(Qso qso) {
        qsos.add(qso);
    }

    public void addActivity(Activity activity) {
        if (activity != null) {
            activities.put(activity.getType(), activity);
        }
    }

    public boolean isDoing(ActivityType type) {
        return activities.containsKey(type);
    }

    public Activity getActivity(ActivityType type) {
        return activities.get(type);
    }

    public boolean hasActivity() { return activities.size() > 0; }

    public boolean doingSameActivityAs(Station other) {
        for (Activity activity : activities.values()) {
            if (other.isDoing(activity.getType())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Stations are considered equal if they have the same callsign and are in the same
     * location (or have no location set)
     * @param other Other instance to compare
     * @return true if same callsign and same location
     */
    @Override
    public boolean equals(Object other) {
        if (other instanceof Station) {
            Station otherStation = (Station) other;
            return otherStation.getKey().equals(getKey());
        }
        return false;
    }

    /**
     * Unique key for this station including callsign and either coords/grid or both/neither
     * @return unique key for this station
     */
    public String getKey() {
        return String.format("%s %s %s", getCallsign(), getCoordinates(), getGrid());
    }
}
