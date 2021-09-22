package uk.m0nom.kml.info;

import org.marsik.ham.adif.enums.Mode;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.adif3.contacts.Station;

public class KmlStationIcon {

    public String getIconFromStation(TransformControl control, Station station) {
        String cs = station.getCallsign();

        String icon = control.getKmlFixedIconUrl();

        // SOTA icon overrides WOTA, so is above it in this list
        for (ActivityType activity : ActivityType.values()) {
            if (station.isDoing(activity)) {
                return control.getActivityIcon(activity);
            }
        }

        if (cs.endsWith("/P")) {
            return control.getKmlPortableIconUrl();
        }
        if (cs.endsWith("/M")) {
            return control.getKmlMobileIconUrl();
        }
        if (cs.endsWith("/MM")) {
            return control.getKmlMaritimeIconUrl();
        }
        return icon;
    }

    public String getIconFromMode(TransformControl control, Mode mode) {
        String iconUrl;
        if (mode == Mode.CW) {
            iconUrl = control.getKmlCwIconUrl();
        } else {
            iconUrl = "";
        }
        return iconUrl;
    }
}
