package uk.m0nom.adifproc.location;

import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.types.Pota;
import org.marsik.ham.adif.types.PotaList;
import org.marsik.ham.adif.types.Sota;
import org.marsik.ham.adif.types.Wwff;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.contacts.Station;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.coords.*;
import uk.m0nom.adifproc.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.adifproc.qrz.CachingQrzXmlService;
import uk.m0nom.adifproc.qrz.QrzCallsign;

import java.util.List;
import java.util.logging.Logger;

@Service
public class FromLocationDeterminer extends BaseLocationDeterminer {
    protected static final Logger logger = Logger.getLogger(FromLocationDeterminer.class.getName());

    public FromLocationDeterminer(CachingQrzXmlService qrzXmlService, ActivityDatabaseService activities) {
        super(qrzXmlService, activities);
    }

    private void setMyLocationFromGrid(Qso qso, String myGrid) {
        Adif3Record rec = qso.getRecord();
        qso.getRecord().setMyGridSquare(StringUtils.substring(myGrid, 0, 8));
        if (myGrid.length() > 8) {
            qso.getRecord().setMyGridsquareExt(myGrid.substring(8));
        }
        qso.getFrom().setGrid(myGrid);
        GlobalCoords3D coords = MaidenheadLocatorConversion.locatorToCoords(LocationSource.QRZ, myGrid, null);
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
            LocationParsingService parsers = new LocationParsingService();
            if (StringUtils.isNotBlank(control.getLocation())) {
                LocationParserResult result = parsers.parseStringForCoordinates(LocationSource.OVERRIDE, control.getLocation());
                if (result.getCoords() != null) {
                    setMyLocationFromCoordinates(qso, result.getCoords());
                    String gridsquare = MaidenheadLocatorConversion.locationParserResultToLocator(result);
                    qso.getFrom().setGrid(gridsquare);
                    qso.getRecord().setMyGridSquare(StringUtils.substring(gridsquare, 0,8));
                    locationSet = true;
                    if (gridsquare.length() > 8) {
                        qso.getRecord().setMyGridsquareExt(gridsquare.substring(8));
                    }
                }

            }
        }
        return locationSet;
    }

    public String setMyLocationFromSotaId(Qso qso, String sotaId) {
        Activity sotaInfo = activities.getDatabase(ActivityType.SOTA).get(sotaId);
        setMyLocationFromActivity(qso.getFrom(), qso.getRecord(), sotaInfo);
        return null;
    }

    private boolean setMyLocationFromActivities(Qso qso) {
        boolean locationSetFromActivity = false;
        for (Activity activity : qso.getFrom().getActivities()) {
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
        if (rec.getMyGridSquare() != null && !MaidenheadLocatorConversion.isADubiousGridSquare(rec.getMyGridSquare())) {
            // Less Accurate from a Gridsquare, but better than nothing
            GlobalCoordinates myLoc = MaidenheadLocatorConversion.locatorToCoords(LocationSource.OVERRIDE, rec.getMyGridSquare());
            rec.setMyCoordinates(myLoc);
            qso.getFrom().setGrid(rec.getMyGridSquare());
            qso.getFrom().setCoordinates(new GlobalCoords3D(myLoc, rec.getMyAltitude() != null ? rec.getMyAltitude() : 0.0));
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
            if (!MaidenheadLocatorConversion.isADubiousGridSquare(callsignData.getGrid())) {
                rec.setMyGridSquare(callsignData.getGrid());
                setMyLocationFromGrid(qso, callsignData.getGrid());
                return true;
            }
        }
        return false;
    }

    public void setMyLocation(TransformControl control, Qso qso, QrzCallsign callsignData) {
        Adif3Record rec = qso.getRecord();

        if (rec.getMyCoordinates() != null) {
            qso.getFrom().setCoordinates(new GlobalCoords3D(rec.getMyCoordinates(), LocationSource.FROM_ADIF, LocationAccuracy.LAT_LONG));
        } else {

            // Query the record MY_SOTA_REF field - if this isn't empty add it as an activity for onward processing
            if (rec.getMySotaRef() != null) {
                String sotaRef = rec.getMySotaRef().getValue().toUpperCase();
                qso.getFrom().addActivity(activities.getDatabase(ActivityType.SOTA).get(sotaRef));
            }

            // Query the record MY_WWFF_REF field - if this isn't empty add it as an activity for onward processing
            if (rec.getMyWwffRef() != null) {
                String wwffRef = rec.getMyWwffRef().getValue().toUpperCase();
                qso.getFrom().addActivity(activities.getDatabase(ActivityType.WWFF).get(wwffRef));
            }

            if (rec.getMyPotaRef() != null) {
                List<Pota> potaList = rec.getMyPotaRef().getPotaList();
                for (Pota pota: potaList) {
                    qso.getFrom().addActivity(activities.getDatabase(ActivityType.POTA).get(pota.getValue()));
                }
            }

            // Update my SIG/SIG_INFO if there is an activity defined
            updateMySigInfoFromActivity(qso);

            if (!setMyLocationFromControl(qso, control)) {
                if (!setMyLocationFromActivities(qso)) {
                    if (!isFromRecGridSquareSameAsQrzLatLongGrid(qso, callsignData)) {
                        setMyLocationFromRecGridsquare(qso);
                    } else {
                        if (!setMyLocationFromQrzLatLong(qso, callsignData)) {
                            setMyLocationFromQrzGrid(qso, callsignData);
                        }
                    }
                }
            }
        }
    }

    private boolean isFromRecGridSquareSameAsQrzLatLongGrid(Qso qso, QrzCallsign callsignData) {
        String recGridSquare = qso.getRecord().getMyGridSquare();
        if (qso.getRecord().getMyGridsquareExt() != null) {
            recGridSquare = recGridSquare + qso.getRecord().getMyGridsquareExt();
        }
        // Calculate grid square from qrz lat long
        if (callsignData != null && callsignData.getLat() != null && callsignData.getLon() != null) {
            String qrzGridSquare = MaidenheadLocatorConversion.coordsToLocator(new GlobalCoordinates((callsignData.getLat()), callsignData.getLon()));
            return recGridSquare.toUpperCase().contains(qrzGridSquare.toUpperCase());
        }
        return false;
    }

    private void updateMySigInfoFromActivity(Qso qso) {
        if (qso.getFrom().getActivities() != null) {
            // We don't bother with SOTA here because it has its own ADIF record
            for (Activity activity : qso.getFrom().getActivities()) {
                if (qso.getRecord().getMySig() == null && !ActivityType.hasOwnAdifField(activity.getType())) {
                    // Can only process one, however. If required the transformer will have to be run multiple times with each SIG defined separately
                    qso.getRecord().setMySig(activity.getType().getActivityName());
                    qso.getRecord().setMySigInfo(activity.getRef());
                    //logger.info(String.format("Setting MYSIG to be: %s with MY_SIGINFO: %s", qso.getRecord().getMySig(), qso.getRecord().getMySigInfo()));
                }
                if (activity.getType() == ActivityType.SOTA && qso.getRecord().getMySotaRef() == null) {
                    Sota sota = Sota.valueOf(activity.getRef().toUpperCase());
                    qso.getRecord().setMySotaRef(sota);
                }
                /* Use ADIF Spec 3.1.3 MY_WWFF_REF */
                if (activity.getType() == ActivityType.WWFF && qso.getRecord().getMyWwffRef() == null) {
                    Wwff wwff = Wwff.valueOf(activity.getRef().toUpperCase());
                    qso.getRecord().setMyWwffRef(wwff);
                }
                /* Use ADIF Spec 3.1.4 MY_POTA_REF - can be a list */
                if (activity.getType() == ActivityType.POTA) {
                    // We keep the POTA list null until there is one
                    if (qso.getRecord().getMyPotaRef() == null) {
                        qso.getRecord().setMyPotaRef(new PotaList());
                    }
                    Pota pota = Pota.valueOf(activity.getRef().toUpperCase());
                    qso.getRecord().getMyPotaRef().addPota(pota);
                }
            }
        }
    }
}
