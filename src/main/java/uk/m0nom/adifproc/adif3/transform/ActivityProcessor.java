package uk.m0nom.adifproc.adif3.transform;

import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityDatabase;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.activity.wota.WotaInfo;
import uk.m0nom.adifproc.activity.wota.WotaSummitsDatabase;
import uk.m0nom.adifproc.adif3.contacts.Station;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.qrz.CachingQrzXmlService;

@Service
public class ActivityProcessor {

    protected final CachingQrzXmlService qrzXmlService;
    protected final ActivityDatabaseService activities;

    public ActivityProcessor(CachingQrzXmlService qrzXmlService, ActivityDatabaseService activities) {
        this.qrzXmlService = qrzXmlService;
        this.activities = activities;
    }

    /**
     * If a Wainwright on the air summit corresponds to a SOTA/HEMA summit then we alsop record those respective
     * activities against the station
     * @param station station participating in Wainwrights on the air
     * @param wotaId Reference for the WOTA, eg: LDO-113
     */
    protected void setHemaOrSotaFromWota(Station station, String wotaId) {
        WotaInfo wotaInfo = (WotaInfo) activities.getDatabase(ActivityType.WOTA).get(wotaId);

        station.addActivity(activities.getDatabase(ActivityType.HEMA).get(wotaInfo.getHemaId()));
        station.addActivity(activities.getDatabase(ActivityType.SOTA).get(wotaInfo.getSotaId()));
    }

    /**
     * Check if a HEMA is being activated by the station if it corresponds to a WOTA summit in the Lake District
     * @param station station participating in HEMA
     * @param hemaId Humps on the Air reference for the summit being activated
     */
    protected void setWotaFromHemaId(Station station, String hemaId) {
        WotaSummitsDatabase wotaDatabase = (WotaSummitsDatabase) activities.getDatabase(ActivityType.WOTA);
        station.addActivity(wotaDatabase.getFromHemaId(hemaId));
    }

    /**
     * Check ifa SOTA is being activated by the station if it corresponds to a WOTA summit in the Lake District
     * @param station station participating in SOTA
     * @param sotaId SOTA reference for the summit being activated
     */
    protected void setWotaFromSotaId(Station station, String sotaId) {
        WotaSummitsDatabase wotaDatabase = (WotaSummitsDatabase) activities.getDatabase(ActivityType.WOTA);
        station.addActivity(wotaDatabase.getFromSotaId(sotaId));
    }

    /**
     * If the 'from' station has activity information specified in the transform control record then add this
     * activity to the station
     * @param type activity type to check
     * @param station station doing the activation
     */
    protected void processActivityFromControl(TransformControl control, ActivityType type, Station station) {
        String ref = control.getActivityRef(type);
        if (StringUtils.isNotBlank(ref)) {
            ref = ref.toUpperCase();
            processActivity(type, station, ref);
        }
    }

    /**
     * This method provides the cross referencing logic for WOTA, HEMA and SOTA to ensure that all activities
     * are recorded where the summit is part of multiple programmes
     * @param type type of activity
     * @param station station participating in a programme
     * @param ref the reference of the activity provided
     */
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

    /**
     * Record activity for a station where the input ADIF file contains a SIG_INFO/SIG_REF record
     * @param type activity type to check
     * @param station station to record activity against
     * @param rec input ADIF record
     */
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

    /**
     * For each of the supported activities ensure any SIG_INFO/SIG_REF information in the input ADIF file is
     * recorded against the station
     * @param station station to check for activity
     * @param rec input ADIF record to check for SIG_INFO/SIG_REF supported activity information
     */
    public void processActivities(TransformControl control, Station station, Adif3Record rec) {
        for (ActivityType activity : ActivityType.values()) {
            processActivityFromSigInfo(activity, station, rec);
            processActivityFromControl(control, activity, station);

            // Some activities are 'special events' where the station callsign is the activity ref
            // So need to check our callsign against the list in these cases
            processSpecialEventActivity(activity, station);
        }

    }

    /**
     * A special event is where the callsign of the station defines the activity being participated in (e.g. Railways
     * on the Air). In this case we need to check the activity references against the station callsign
     * @param type activity type to check against
     * @param station the station to check the callsign against activity references
     */
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
