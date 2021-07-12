package uk.m0nom.adif3.contacts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
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
    private GlobalCoordinates coordinates;

    public Station() {
        activities = new HashMap<>();
        qsos = new ArrayList<>();
    }

    public Station(String callsign, Qso initialQso) {
        this();
        this.callsign = callsign;
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
        return activities.keySet().contains(type);
    }

    public Activity getActivity(ActivityType type) {
        return activities.get(type);
    }

    public boolean doingSameActivityAs(Station other) {
        for (Activity activity : activities.values()) {
            if (other.isDoing(activity.getType())) {
                return true;
            }
        }
        return false;
    }
}
