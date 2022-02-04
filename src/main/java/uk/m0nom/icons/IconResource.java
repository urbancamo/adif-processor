package uk.m0nom.icons;

import lombok.Getter;
import org.marsik.ham.adif.enums.Mode;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.adif3.contacts.Station;

@Getter
public class IconResource {
    public final static String FIXED_ICON_NAME = "fixed";
    public final static String PORTABLE_ICON_NAME = "portable";
    public final static String MOBILE_ICON_NAME = "mobile";
    public final static String MARITIME_MOBILE_ICON_NAME = "maritime";
    public final static String CW_ICON_NAME = "cw";
    public final static String SSB_ICON_NAME = "ssb";
    public final static String FM_ICON_NAME = "fm";
    public final static String DEFAULT_MODE_ICON_NAME = "mode";
    public final static String SATELLITE_ICON_NAME="satellite";
    public final static String SATELLITE_TRACK_ICON_NAME="satellite_track";

    public final static String FIXED_DEFAULT_ICON_URL = "https://maps.google.com/mapfiles/kml/shapes/ranger_station.png";
	public final static String PORTABLE_DEFAULT_ICON_URL = "https://maps.google.com/mapfiles/kml/shapes/hiker.png";
	public final static String MOBILE_DEFAULT_ICON_URL = "https://maps.google.com/mapfiles/kml/shapes/cabs.png";
    public final static String MARITIME_DEFAULT_ICON_URL = "https://maps.google.com/mapfiles/kml/shapes/sailing.png";
    public final static String SATELLITE_DEFAULT_ICON_URL = "http://maps.google.com/mapfiles/kml/shapes/placemark_circle_highlight.png";
    public final static String SATELLITE_TRACK_DEFAULT_ICON_URL = "http://maps.google.com/mapfiles/kml/shapes/capital_big_highlight.png";

	public final static String POTA_DEFAULT_ICON_URL = "https://maps.google.com/mapfiles/kml/shapes/picnic.png";
	public final static String SOTA_DEFAULT_ICON_URL = "https://maps.google.com/mapfiles/kml/shapes/mountains.png";
    public final static String HEMA_DEFAULT_ICON_URL = "https://maps.google.com/mapfiles/kml/shapes/hospitals.png";
    public final static String WOTA_DEFAULT_ICON_URL = "https://maps.google.com/mapfiles/kml/shapes/trail.png";
    public final static String WWFF_DEFAULT_ICON_URL = "https://maps.google.com/mapfiles/kml/shapes/parks.png";
    public final static String COTA_DEFAULT_ICON_URL = "https://maps.google.com/mapfiles/kml/shapes/schools.png";
    public final static String LOTA_DEFAULT_ICON_URL = "https://maps.google.com/mapfiles/kml/shapes/marina.png";
    public final static String ROTA_DEFAULT_ICON_URL = "https://maps.google.com/mapfiles/kml/shapes/rail.png";
    public final static String IOTA_DEFAULT_ICON_URL = "https://maps.google.com/mapfiles/kml/shapes/info.png";
    public static final String CW_DEFAULT_ICON_URL = "";

    private final String name;
    private final String url;

    private IconResource(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public static IconResource getSatelliteTrackResource(TransformControl control) {
        return new IconResource(SATELLITE_TRACK_ICON_NAME, control.getIcon(SATELLITE_TRACK_ICON_NAME));
    }

    public static IconResource getSatelliteResource(TransformControl control) {
        return new IconResource(SATELLITE_ICON_NAME, control.getIcon(SATELLITE_ICON_NAME));
    }

    public static IconResource getIconFromStation(TransformControl control, Station station) {
        String cs = station.getCallsign();

        IconResource icon = new IconResource(FIXED_ICON_NAME, control.getIcon(FIXED_ICON_NAME));

        // SOTA icon overrides WOTA, so is above it in this list
        for (ActivityType activity : ActivityType.values()) {
            if (station.isDoing(activity)) {
                return new IconResource(activity.getActivityName(), control.getIcon(activity.getActivityName()));
            }
        }

        if (cs.endsWith("/P")) {
            return new IconResource(PORTABLE_ICON_NAME, control.getIcon(PORTABLE_ICON_NAME));
        }
        if (cs.endsWith("/M")) {
            return new IconResource(MOBILE_ICON_NAME, control.getIcon(MOBILE_ICON_NAME));
        }
        if (cs.endsWith("/MM")) {
            return new IconResource(MARITIME_MOBILE_ICON_NAME, control.getIcon(MARITIME_MOBILE_ICON_NAME));
        }
        return icon;
    }

    public static IconResource getIconFromMode(TransformControl control, Mode mode) {
        IconResource icon;
        switch (mode) {
            case CW:
                icon = new IconResource(CW_ICON_NAME, control.getIcon(CW_ICON_NAME));
                break;
            case SSB:
                icon = new IconResource(SSB_ICON_NAME, control.getIcon(SSB_ICON_NAME));
                break;
            case FM:
                icon = new IconResource(FM_ICON_NAME, control.getIcon(FM_ICON_NAME));
                break;
            default:
                icon = new IconResource(DEFAULT_MODE_ICON_NAME, control.getIcon(DEFAULT_MODE_ICON_NAME));
                break;
        }
        return icon;
    }
}
