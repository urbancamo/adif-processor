package uk.m0nom.location;

import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.wota.WotaSummitInfo;
import uk.m0nom.activity.wota.WotaSummitsDatabase;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.adif3.contacts.Station;
import uk.m0nom.qrz.QrzXmlService;

import java.util.logging.Logger;

public class BaseLocationDeterminer {
    private static final Logger logger = Logger.getLogger(BaseLocationDeterminer.class.getName());

    protected final TransformControl control;
    protected final QrzXmlService qrzXmlService;
    protected final ActivityDatabases activities;

    protected boolean reportedLocationOverride = false;

    public BaseLocationDeterminer(TransformControl control, QrzXmlService qrzXmlService, ActivityDatabases activities) {
        this.control = control;
        this.qrzXmlService = qrzXmlService;
        this.activities = activities;
    }

    protected void setHemaOrSotaFromWota(Station station, String wotaId) {
        WotaSummitInfo wotaInfo = (WotaSummitInfo) activities.getDatabase(ActivityType.WOTA).get(wotaId);

        station.addActivity(activities.getDatabase(ActivityType.HEMA).get(wotaInfo.getHemaId()));
        station.addActivity(activities.getDatabase(ActivityType.SOTA).get(wotaInfo.getSotaId()));
    }

    protected void setWotaFromHemaId(Station station, String hemaId) {
        WotaSummitsDatabase wotaDatabase = (WotaSummitsDatabase) activities.getDatabase(ActivityType.WOTA);
        station.addActivity(wotaDatabase.getFromHemaId(hemaId));
    }

    protected void setWotaFromSotaId(Station station, String sotaId) {
        WotaSummitsDatabase wotaDatabase = (WotaSummitsDatabase) activities.getDatabase(ActivityType.WOTA);
        station.addActivity(wotaDatabase.getFromSotaId(sotaId));
    }

    protected void processActivity(ActivityType type, Station station) {
        String ref = control.getActivityRef(type);
        if (StringUtils.isNotBlank(ref)) {
            ref = ref.toUpperCase();
            station.addActivity(activities.getDatabase(type).get(ref.toUpperCase()));
            // Special handling where there can be multiple activities from the same location
            switch (type) {
                case WOTA:
                    setHemaOrSotaFromWota(station, ref);
                    break;
                case SOTA:
                    setWotaFromSotaId(station, ref);
                    break;
                case HEMA:
                    setWotaFromHemaId(station, ref);
                    break;
            }
        }
    }

    protected void reportLocationOverride(String stationCallsign, String grid) {
        if (!reportedLocationOverride) {
            logger.info(String.format("Overriding location of %s to grid: %s",
                    stationCallsign, grid));
            reportedLocationOverride = true;
        }
    }

    protected void reportLocationOverride(String stationCallsign, double latitude, double longitude) {
        if (!reportedLocationOverride) {
            logger.info(String.format("Overriding location of %s to lat: %.3f, long: %.3f",
                    stationCallsign, latitude, longitude));
            reportedLocationOverride = true;
        }
    }

}
