package uk.m0nom.adifproc.location;

import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.types.Sota;
import org.marsik.ham.adif.types.Wwff;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.activity.wota.WotaInfo;
import uk.m0nom.adifproc.activity.wota.WotaSummitsDatabase;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.coords.LocationSource;
import uk.m0nom.adifproc.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.adifproc.qrz.CachingQrzXmlService;

@Service
public class ToLocationDeterminer extends BaseLocationDeterminer {

    public ToLocationDeterminer(CachingQrzXmlService qrzXmlService, ActivityDatabaseService activities) {
        super(qrzXmlService, activities);
    }

    public String setTheirLocationFromActivity(Qso qso, ActivityType activity, String reference) {
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
            if (activity == ActivityType.SOTA) {
                rec.setSotaRef(Sota.valueOf(reference));
            } else if (activity == ActivityType.WWFF) {
                rec.setWwffRef(Wwff.valueOf(reference));
            } else if (activity != ActivityType.POTA) {
                if (StringUtils.isEmpty(rec.getSig())) {
                    // If the SIG isn't set, add it here now
                    rec.setSig(activity.getActivityName());
                    rec.setSigInfo(reference);
                }
            }
        } else {
            return String.format(BAD_ACTIVITY_REPORT, qso.getTo().getCallsign(), activity.getActivityName(), reference);
        }
        return null;
    }

    public String setTheirLocationFromSotaId(Qso qso, String sotaId) {
        setTheirLocationFromActivity(qso, ActivityType.SOTA, sotaId);
        Activity sotaInfo = activities.getDatabase(ActivityType.SOTA).get(sotaId);
        if (sotaInfo != null) {
            // See if this is also a WOTA
            WotaSummitsDatabase wotaSummitsDatabase = (WotaSummitsDatabase) activities.getDatabase(ActivityType.WOTA);
            Activity wotaInfo = wotaSummitsDatabase.getFromSotaId(sotaId);
       } else {
            return String.format(BAD_ACTIVITY_REPORT, qso.getTo().getCallsign(), "SOTA", sotaId);
        }
        return null;
    }

    public String setTheirLocationFromWotaId(Qso qso, String wotaId) {
        setTheirLocationFromActivity(qso, ActivityType.WOTA, wotaId);
        WotaInfo wotaInfo = (WotaInfo) activities.getDatabase(ActivityType.WOTA).get(wotaId);
        if (wotaInfo != null) {
            String sotaId = wotaInfo.getSotaId();
            if (sotaId != null) {
                // SOTA Latitude/Longitude is more accurate, so overwrite from that information
                setTheirLocationFromSotaId(qso, sotaId);
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
