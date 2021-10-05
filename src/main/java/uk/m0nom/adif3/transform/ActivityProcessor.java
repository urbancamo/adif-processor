package uk.m0nom.adif3.transform;

import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.wota.WotaSummitInfo;
import uk.m0nom.activity.wota.WotaSummitsDatabase;
import uk.m0nom.adif3.contacts.Station;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.qrz.QrzXmlService;

public class ActivityProcessor {

    protected final TransformControl control;
    protected final QrzXmlService qrzXmlService;
    protected final ActivityDatabases activities;

    public ActivityProcessor(TransformControl control, QrzXmlService qrzXmlService, ActivityDatabases activities) {
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

    protected void processActivityFromControl(ActivityType type, Station station) {
        String ref = control.getActivityRef(type);
        if (StringUtils.isNotBlank(ref)) {
            ref = ref.toUpperCase();
            processActivity(type, station, ref);
        }
    }

    protected void processActivity(ActivityType type, Station station, String ref) {
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

    protected void processActivityFromSigInfo(ActivityType type, Station station, Adif3Record rec) {
        String sig = rec.getMySig();
        if (sig == null) {
            sig = rec.getMySigIntl();
        }
        String sigInfo = rec.getMySigInfo();
        if (sigInfo == null) {
            sigInfo = rec.getMySigInfoIntl();
        }

        if (StringUtils.equalsIgnoreCase(type.getActivityName(), sig) && StringUtils.isNotBlank(sigInfo)) {
            processActivity(type, station, sigInfo);
        }
    }

    public void processActivities(Station station, Adif3Record rec) {
        for (ActivityType activity : ActivityType.values()) {
            processActivityFromSigInfo(activity, station, rec);
            processActivityFromControl(activity, station);

            // Some activities are 'special events' where the station callsign is the activity ref
            // So need to check our callsign against the list in these cases
            processSpecialEventActivity(activity, station);
        }

    }

    private void processSpecialEventActivity(ActivityType type, Station station) {
        ActivityDatabase db = activities.getDatabase(type);
        if (db.isSpecialEventActivity()) {
            Activity activity = db.get(station.getCallsign().toUpperCase().trim());
            if (activity != null) {
                station.addActivity(activity);
            }
        }
    }

}
