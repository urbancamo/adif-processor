package uk.m0nom.location;

import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.qrz.QrzService;

public class BaseLocationDeterminer {

    protected final TransformControl control;
    protected final QrzService qrzService;
    protected final ActivityDatabases activities;

    public BaseLocationDeterminer(TransformControl control, QrzService qrzService, ActivityDatabases activities) {
        this.control = control;
        this.qrzService = qrzService;
        this.activities = activities;
    }
}
