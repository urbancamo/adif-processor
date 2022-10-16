package uk.m0nom.adifproc.location;

import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.qrz.QrzService;

public class BaseLocationDeterminer {
    protected final static String BAD_ACTIVITY_REPORT = "%s: (%s %s invalid)";

    protected final QrzService qrzService;
    protected final ActivityDatabaseService activities;

    public BaseLocationDeterminer(QrzService qrzService, ActivityDatabaseService activities) {
        this.qrzService = qrzService;
        this.activities = activities;
    }
}
