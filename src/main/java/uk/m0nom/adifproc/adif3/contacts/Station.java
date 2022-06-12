package uk.m0nom.adifproc.adif3.contacts;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.antenna.Antenna;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.qrz.QrzCallsign;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * One end of a QSO this captures both station related information and the QSOs that the station has participated in
 * For the purposes of comparison a station is considered unique if it is both the same callsign and location
 */
@Data
public class Station {
    private String callsign;
    private QrzCallsign qrzInfo;
    private List<Qso> qsos;

    private Map<ActivityType, Activity> activities;
    private String grid;
    private GlobalCoords3D coordinates;
    private Antenna antenna;

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
        return activities.values().stream().anyMatch(a -> other.isDoing(a.getType()));
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
            return ((Station) other).getKey().equals(getKey());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return getKey().hashCode();
    }

    /**
     * Unique key for this station including callsign and either coords/grid or both/neither
     * @return unique key for this station
     */
    public String getKey() {
        return String.format("%s %s %s", getCallsign(), getCoordinates(), getGrid());
    }
}
