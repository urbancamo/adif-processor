package uk.m0nom.location;

import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.qrz.QrzXmlService;

import java.util.logging.Logger;

public class BaseLocationDeterminer {
    private static final Logger logger = Logger.getLogger(BaseLocationDeterminer.class.getName());

    protected boolean reportedLocationOverride = false;

    protected final TransformControl control;
    protected final QrzXmlService qrzXmlService;
    protected final ActivityDatabases activities;

    public BaseLocationDeterminer(TransformControl control, QrzXmlService qrzXmlService, ActivityDatabases activities) {
        this.control = control;
        this.qrzXmlService = qrzXmlService;
        this.activities = activities;
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
