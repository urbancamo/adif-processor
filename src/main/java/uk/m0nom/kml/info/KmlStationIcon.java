package uk.m0nom.kml.info;

import org.marsik.ham.adif.enums.Mode;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.adif3.contacts.Station;

public class KmlStationIcon {

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
        if (station.isDoing(ActivityType.WWFF)) {
            return control.getKmlWwffIconUrl();
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
        String iconUrl = null;
        switch (mode) {
            case CW:
                iconUrl = control.getKmlCwIconUrl();
                break;
            default:
                iconUrl = "";
                break;

        }
        return iconUrl;
    }
}
