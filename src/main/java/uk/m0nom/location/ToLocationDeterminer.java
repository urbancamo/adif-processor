package uk.m0nom.location;

import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.wota.WotaSummitInfo;
import uk.m0nom.activity.wota.WotaSummitsDatabase;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.qrz.QrzXmlService;

import java.util.Map;

public class ToLocationDeterminer extends BaseLocationDeterminer {
    public ToLocationDeterminer(TransformControl control, QrzXmlService qrzXmlService, ActivityDatabases activities) {
        super(control, qrzXmlService, activities);
    }

    public void setTheirLocationFromActivity(Qso qso, ActivityType activity, String reference, Map<String, String> unmapped) {
        Activity info = activities.getDatabase(activity).get(reference);
        Adif3Record rec = qso.getRecord();
        if (info != null) {
            if (info.hasCoords()) {
                rec.setCoordinates(info.getCoords());
                String grid = MaidenheadLocatorConversion.coordsToLocator(info.getCoords());
                rec.setGridsquare(grid);
                qso.getTo().setGrid(grid);
            } else if (info.hasGrid()) {
                GlobalCoordinates coords = MaidenheadLocatorConversion.locatorToCoords(info.getGrid());
                rec.setMyCoordinates(coords);
                rec.setGridsquare(info.getGrid());
                qso.getTo().setGrid(info.getGrid());
            }
            // If the SIG isn't set, add it here
            if (StringUtils.isEmpty(rec.getSig())) {
                rec.setSig(activity.getActivityName());
                rec.setSigInfo(reference);
            }
            // Add the activity to the unmapped list
            unmapped.put(activity.getActivityName(), reference);
        }
    }

    public void setTheirCoordFromSotaId(Qso qso, String sotaId, Map<String, String> unmapped) {
        setTheirLocationFromActivity(qso, ActivityType.SOTA, sotaId, unmapped);
        Activity sotaInfo = activities.getDatabase(ActivityType.SOTA).get(sotaId);
        if (sotaInfo != null) {
            // See if this is also a WOTA
            WotaSummitsDatabase wotaSummitsDatabase = (WotaSummitsDatabase) activities.getDatabase(ActivityType.WOTA);
            Activity wotaInfo = wotaSummitsDatabase.getFromSotaId(sotaId);
            if (wotaInfo != null) {
                unmapped.put("WOTA", wotaInfo.getRef());
            }
        }
    }

    public void setTheirCoordFromWotaId(Qso qso, String wotaId, Map<String, String> unmapped) {
        setTheirLocationFromActivity(qso, ActivityType.WOTA, wotaId, unmapped);
        WotaSummitInfo wotaInfo = (WotaSummitInfo) activities.getDatabase(ActivityType.WOTA).get(wotaId);
        if (wotaInfo != null) {
            String sotaId = wotaInfo.getSotaId();
            if (sotaId != null) {
                // SOTA Latitude/Longitude is more accurate, so overwrite from that information
                setTheirCoordFromSotaId(qso, sotaId, unmapped);
            } else {
                unmapped.put("WOTA", wotaInfo.getRef());
            }
        }
    }

    public void setTheirLocationFromActivity(Qso qso, Activity activity) {
        if (activity.hasCoords()) {
            GlobalCoordinates coords = activity.getCoords();
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
