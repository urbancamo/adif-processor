package uk.m0nom.adifproc.adif3.contacts;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.antenna.Antenna;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.dxcc.DxccEntity;
import uk.m0nom.adifproc.qrz.QrzCallsign;

import java.util.*;
import java.util.stream.Collectors;

/**
 * One end of a QSO this captures both station related information and the QSOs that the station has participated in
 * For the purposes of comparison a station is considered unique if it is both the same callsign and location
 */
@Data
public class Station {
    private String callsign;
    private QrzCallsign qrzInfo;
    private DxccEntity dxccEntity;
    private List<Qso> qsos;

    private Set<ActivityType> doingActivity;
    private Collection<Activity> activities;
    private String grid;
    private GlobalCoords3D coordinates;
    private Antenna antenna;

    public Station() {
        doingActivity = new HashSet<>();
        activities = new ArrayList<>();
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
            if (!activities.contains(activity)) {
                activities.add(activity);
                doingActivity.add(activity.getType());
            }
        }
    }

    public boolean isDoing(ActivityType type) {
        return doingActivity.contains(type);
    }

    public Collection<Activity> getActivity(ActivityType type) {
        return activities.stream().filter(activity -> activity.getType() == type).collect(Collectors.toCollection(ArrayList::new));
    }

    public boolean hasActivity() { return activities.size() > 0; }

    public boolean doingSameActivityAs(Station other) {
        return activities.stream().anyMatch(a -> other.isDoing(a.getType()));
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
