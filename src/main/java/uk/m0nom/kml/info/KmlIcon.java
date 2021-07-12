package uk.m0nom.kml.info;

import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.adif3.contacts.Station;

public class KmlIcon {

    public String getIconFromStation(TransformControl control, Station station) {
        String cs = station.getCallsign();

        String icon = control.getKmlFixedIconUrl();

        // SOTA icon overrides WOTA, so is above it in this list
        if (station.isDoing(ActivityType.SOTA)) {
            return control.getKmlSotaIconUrl();
        }
        if (station.isDoing(ActivityType.POTA)) {
            return control.getKmlParkIconUrl();
        }
        // HEMA icon overrides WOTA, so is above it in this list
        if (station.isDoing(ActivityType.HEMA)) {
            return control.getKmlHemaIconUrl();
        }
        if (station.isDoing(ActivityType.WOTA)) {
            return control.getKmlWotaIconUrl();
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
}
