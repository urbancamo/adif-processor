package uk.m0nom.location;

import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.types.Sota;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.contacts.Station;
import uk.m0nom.coords.*;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.qrz.QrzCallsign;
import uk.m0nom.qrz.QrzService;

import java.util.logging.Logger;

public class FromLocationDeterminer extends BaseLocationDeterminer {
    protected static final Logger logger = Logger.getLogger(FromLocationDeterminer.class.getName());

    public FromLocationDeterminer(TransformControl control, QrzService qrzService, ActivityDatabases activities) {
        super(control, qrzService, activities);
    }

    private void setMyLocationFromGrid(Qso qso, LocationSource source, String myGrid) {
        Adif3Record rec = qso.getRecord();
        qso.getRecord().setMyGridSquare(myGrid.substring(0, 6));
        qso.getFrom().setGrid(myGrid);
        GlobalCoords3D coords = MaidenheadLocatorConversion.locatorToCoords(source, myGrid);
        rec.setMyCoordinates(coords);
        qso.getFrom().setCoordinates(coords);
    }


    private void setMyLocationFromCoordinates(Qso qso, GlobalCoords3D coords) {
        qso.getRecord().setMyCoordinates(coords);
        qso.getFrom().setCoordinates(coords);
    }

    private boolean setMyLocationFromControl(Qso qso, TransformControl control) {
        boolean locationSet = false;
        if (control.getLocation() != null) {
            LocationParsers parsers = new LocationParsers();
            if (StringUtils.isNotBlank(control.getLocation())) {
                LocationParserResult result = parsers.parseStringForCoordinates(LocationSource.OVERRIDE, control.getLocation());
                if (result.getCoords() != null) {
                    setMyLocationFromCoordinates(qso, result.getCoords());
                    String gridsquare = MaidenheadLocatorConversion.coordsToLocator(result.getCoords());
                    qso.getFrom().setGrid(gridsquare);
                    qso.getRecord().setMyGridSquare(gridsquare);
                    locationSet = true;
                }
            }
        }
        return locationSet;
    }

    private boolean setMyLocationFromActivities(Qso qso) {
        boolean locationSetFromActivity = false;
        for (Activity activity : qso.getFrom().getActivities().values()) {
            setMyLocationFromActivity(qso.getFrom(), qso.getRecord(), activity);
            locationSetFromActivity = true;
        }
        return locationSetFromActivity;
    }

    private void setMyGridFromCoords(Station station, Adif3Record rec, GlobalCoordinates coords) {
        String grid = MaidenheadLocatorConversion.coordsToLocator(coords);
        rec.setMyGridSquare(grid);
        station.setGrid(grid);
    }

    private void setMyCoordsFromGrid(Station station, Adif3Record rec, String grid) {
        GlobalCoords3D coords = MaidenheadLocatorConversion.locatorToCoords(LocationSource.ACTIVITY, grid);
        rec.setMyCoordinates(coords);
        station.setCoordinates(coords);
    }

    private void setMyLocationFromActivity(Station station, Adif3Record rec, Activity info) {
        if (info.hasCoords()) {
            rec.setMyCoordinates(info.getCoords());
            station.setCoordinates(info.getCoords());
            setMyGridFromCoords(station, rec, info.getCoords());
        } else if (info.hasGrid()) {
            rec.setMyGridSquare(info.getGrid());
            setMyCoordsFromGrid(station, rec, info.getGrid());
        } else {
            logger.warning(String.format("Your activity %s at %s doesn't have a location defined", info.getType().getActivityName(), info.getRef()));
        }
    }

    private boolean setMyLocationFromRecGridsquare(Qso qso) {
        Adif3Record rec = qso.getRecord();
        if (rec.getMyGridSquare() != null && MaidenheadLocatorConversion.isAValidGridSquare(rec.getMyGridSquare())) {
            // Less Accurate from a Gridsquare, but better than nothing
            GlobalCoordinates myLoc = MaidenheadLocatorConversion.locatorToCoords(LocationSource.OVERRIDE, rec.getMyGridSquare());
            rec.setMyCoordinates(myLoc);
            qso.getFrom().setGrid(rec.getMyGridSquare());
            return true;
        }
        return false;
    }

    private boolean setMyLocationFromQrzLatLong(Qso qso, QrzCallsign callsignData) {
        Adif3Record rec = qso.getRecord();
        if (callsignData != null && callsignData.getLat() != null && callsignData.getLon() != null) {
            GlobalCoords3D coord = new GlobalCoords3D(callsignData.getLat(), callsignData.getLon());
            rec.setMyCoordinates(coord);
            qso.getFrom().setCoordinates(coord);
            return true;
        }
        return false;
    }

    private boolean setMyLocationFromQrzGrid(Qso qso, QrzCallsign callsignData) {
        Adif3Record rec = qso.getRecord();
        if (callsignData != null && callsignData.getGrid() != null) {
            if (MaidenheadLocatorConversion.isAValidGridSquare(callsignData.getGrid())) {
                rec.setMyGridSquare(callsignData.getGrid());
                setMyLocationFromGrid(qso, LocationSource.QRZ, callsignData.getGrid());
                return true;
            }
        }
        return false;
    }

    public boolean setMyLocation(Qso qso, QrzCallsign callsignData) {
        Adif3Record rec = qso.getRecord();
        boolean locationSet = true;

        // Query the record MYSOTA_REF field - if this isn't empty add it as an activity for onward processing
        if (rec.getMySotaRef() != null) {
            String sotaRef = rec.getMySotaRef().getValue().toUpperCase();
            qso.getFrom().addActivity(activities.getDatabase(ActivityType.SOTA).get(sotaRef));
        }

        // Update my SIG/SIG_INFO if there is an activity defined
        updateMySigInfoFromActivity(qso);

        if (!setMyLocationFromControl(qso, control)) {
            if (!setMyLocationFromActivities(qso)) {
                if (!setMyLocationFromRecGridsquare(qso)) {
                    if (!setMyLocationFromQrzLatLong(qso, callsignData)) {
                        if (!setMyLocationFromQrzGrid(qso, callsignData)) {
                            locationSet = false;
                        }
                    }
                }
            }
        }
        return locationSet;
    }

    private void updateMySigInfoFromActivity(Qso qso) {
        if (qso.getRecord().getMySig() == null && qso.getFrom().getActivities() != null) {
            // We don't bother with SOTA here because it has its own ADIF record
            for (Activity activity : qso.getFrom().getActivities().values()) {
                if (activity.getType() != ActivityType.SOTA) {
                    // Can only process one, however. If required the transformer will have to be run multiple times with each SIG defined separately
                    qso.getRecord().setMySig(activity.getType().getActivityName());
                    qso.getRecord().setMySigInfo(activity.getRef());
                    //logger.info(String.format("Setting MYSIG to be: %s with MY_SIGINFO: %s", qso.getRecord().getMySig(), qso.getRecord().getMySigInfo()));
                    return;
                } else if (qso.getRecord().getMySotaRef() == null) {
                    Sota sota = Sota.valueOf(activity.getRef().toUpperCase());
                    qso.getRecord().setMySotaRef(sota);
                }
            }
        }
    }
}
