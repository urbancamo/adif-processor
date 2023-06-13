package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.apache.commons.lang3.StringUtils;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.types.Sota;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.location.FromLocationDeterminer;
import uk.m0nom.adifproc.location.ToLocationDeterminer;

public class MySotaRefFieldParser implements CommentFieldParser {
    private final FromLocationDeterminer fromLocationDeterminer;
    private final ActivityDatabaseService activities;

    public MySotaRefFieldParser(FromLocationDeterminer fromLocationDeterminer, ActivityDatabaseService activities) {
        this.fromLocationDeterminer = fromLocationDeterminer;
        this.activities = activities;
    }

    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        Adif3Record rec = qso.getRecord();
        FieldParseResult result;

        // Strip off any S2s reference
        String sotaRef = StringUtils.split(value, ' ')[0];
        try {
            Sota sota = Sota.valueOf(sotaRef.toUpperCase());
            rec.setMySotaRef(sota);
            String invalidCallsign = fromLocationDeterminer.setMyLocationFromSotaId(qso, sotaRef);
            result = new FieldParseResult(invalidCallsign, false);
            qso.getFrom().addActivity(activities.getDatabase(ActivityType.SOTA).get(sotaRef));
        } catch (IllegalArgumentException iae) {
            throw new CommentFieldParserException(this.getClass().getName(), "notValidActivityRef", qso, iae, true, value, rec.getCall(), rec.getTimeOn().toString());
        }
        return result;
    }
}
