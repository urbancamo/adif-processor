package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.types.Iota;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.location.ToLocationDeterminer;

public class IotaFieldParser implements CommentFieldParser {
    protected final ToLocationDeterminer toLocationDeterminer;
    protected final ActivityDatabaseService activities;
    protected final ActivityType activityType;

    public IotaFieldParser(ToLocationDeterminer toLocationDeterminer, ActivityDatabaseService activities, ActivityType activityType) {
        this.toLocationDeterminer = toLocationDeterminer;
        this.activities = activities;
        this.activityType = activityType;
    }

    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        Adif3Record rec = qso.getRecord();
        try {
            Iota iota = Iota.findByCode(value);
            rec.setIota(iota);
            qso.getTo().addActivity(activities.getDatabase(activityType).get(value));
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, e, true, value, rec.getCall(), rec.getTimeOn().toString());
        }
        return FieldParseResult.SUCCESS;
    }
}
