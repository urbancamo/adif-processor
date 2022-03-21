package uk.m0nom.adif3.transform.comment.parsers;

import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.location.ToLocationDeterminer;

public class ActivityFieldParser implements CommentFieldParser {
    protected final ToLocationDeterminer toLocationDeterminer;
    protected final ActivityDatabases activities;
    protected final ActivityType activityType;

    public ActivityFieldParser(ToLocationDeterminer toLocationDeterminer, ActivityDatabases activities, ActivityType activityType) {
        this.toLocationDeterminer = toLocationDeterminer;
        this.activities = activities;
        this.activityType = activityType;
    }

    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        String activityId = StringUtils.split(value, ' ')[0];
        String callsignWithInvalidActivity = toLocationDeterminer.setTheirLocationFromActivity(qso, activityType, activityId.toUpperCase());
        qso.getTo().addActivity(activities.getDatabase(activityType).get(activityId));
        return new FieldParseResult(callsignWithInvalidActivity, true);
    }
}
