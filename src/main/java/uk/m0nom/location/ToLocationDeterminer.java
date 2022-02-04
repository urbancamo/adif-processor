package uk.m0nom.location;

import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.wota.WotaInfo;
import uk.m0nom.activity.wota.WotaSummitsDatabase;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.coords.GlobalCoords3D;
import uk.m0nom.coords.LocationSource;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.qrz.QrzService;

import java.util.Map;

public class ToLocationDeterminer extends BaseLocationDeterminer {
    private final static String BAD_ACTIVITY_REPORT = "%s: (%s %s invalid)";
    public ToLocationDeterminer(TransformControl control, QrzService qrzService, ActivityDatabases activities) {
        super(control, qrzService, activities);
    }

    public String setTheirLocationFromActivity(Qso qso, ActivityType activity, String reference, Map<String, String> unmapped) {
        Activity info = activities.getDatabase(activity).get(reference);
        Adif3Record rec = qso.getRecord();
        if (info != null) {
            if (info.hasCoords()) {
                rec.setCoordinates(info.getCoords());
                String grid = MaidenheadLocatorConversion.coordsToLocator(info.getCoords());
                rec.setGridsquare(grid);
                qso.getTo().setGrid(grid);
                qso.getTo().setCoordinates(info.getCoords());
            } else if (info.hasGrid()) {
                GlobalCoords3D coords = MaidenheadLocatorConversion.locatorToCoords(LocationSource.ACTIVITY, info.getGrid());
                rec.setCoordinates(coords);
                rec.setGridsquare(info.getGrid());
                qso.getTo().setGrid(info.getGrid());
                qso.getTo().setCoordinates(coords);
            }
            // If the SIG isn't set, add it here
            if (StringUtils.isEmpty(rec.getSig())) {
                rec.setSig(activity.getActivityName());
                rec.setSigInfo(reference);
            }
            // Add the activity to the unmapped list
            unmapped.put(activity.getActivityName(), reference);
        } else {
            return String.format(BAD_ACTIVITY_REPORT, qso.getTo().getCallsign(), activity.getActivityName(), reference);
        }
        return null;
    }

    public String setTheirLocationFromSotaId(Qso qso, String sotaId, Map<String, String> unmapped) {
        setTheirLocationFromActivity(qso, ActivityType.SOTA, sotaId, unmapped);
        Activity sotaInfo = activities.getDatabase(ActivityType.SOTA).get(sotaId);
        if (sotaInfo != null) {
            // See if this is also a WOTA
            WotaSummitsDatabase wotaSummitsDatabase = (WotaSummitsDatabase) activities.getDatabase(ActivityType.WOTA);
            Activity wotaInfo = wotaSummitsDatabase.getFromSotaId(sotaId);
            if (wotaInfo != null) {
                unmapped.put("WOTA", wotaInfo.getRef());
            }
        } else {
            return String.format(BAD_ACTIVITY_REPORT, qso.getTo().getCallsign(), "SOTA", sotaId);
        }
        return null;
    }

    public String setTheirLocationFromWotaId(Qso qso, String wotaId, Map<String, String> unmapped) {
        setTheirLocationFromActivity(qso, ActivityType.WOTA, wotaId, unmapped);
        WotaInfo wotaInfo = (WotaInfo) activities.getDatabase(ActivityType.WOTA).get(wotaId);
        if (wotaInfo != null) {
            String sotaId = wotaInfo.getSotaId();
            if (sotaId != null) {
                // SOTA Latitude/Longitude is more accurate, so overwrite from that information
                setTheirLocationFromSotaId(qso, sotaId, unmapped);
            } else {
                unmapped.put("WOTA", wotaInfo.getRef());
            }
        } else {
            return String.format(BAD_ACTIVITY_REPORT, qso.getTo().getCallsign(), "WOTA", wotaId);
        }
        return null;
    }

    public void setTheirLocationFromActivity(Qso qso, Activity activity) {
        // Do nothing here if the location is already overridden via a COORD comment in the ADIF record
        if (qso.getTo().getCoordinates() != null && qso.getTo().getCoordinates().getLocationInfo().getSource() == LocationSource.OVERRIDE) {
            return;
        }

        if (activity.hasCoords()) {
            GlobalCoords3D coords = activity.getCoords();
            String grid = MaidenheadLocatorConversion.coordsToLocator(coords);

            qso.getTo().setCoordinates(coords);
            qso.getRecord().setCoordinates(coords);
            qso.getTo().setGrid(grid);
            qso.getRecord().setGridsquare(grid);
        } else if (activity.hasGrid()) {
            String grid = activity.getGrid();
            qso.getTo().setGrid(grid);
            qso.getRecord().setGridsquare(grid);
        }
    }

}
