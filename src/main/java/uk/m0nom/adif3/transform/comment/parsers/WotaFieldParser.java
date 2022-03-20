package uk.m0nom.adif3.transform.comment.parsers;

import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.location.ToLocationDeterminer;

public class WotaFieldParser extends ActivityFieldParser implements CommentFieldParser {

    public WotaFieldParser(ToLocationDeterminer toLocationDeterminer, ActivityDatabases activities) {
        super(toLocationDeterminer, activities, ActivityType.WOTA);
    }

    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {

        String wotaId = StringUtils.split(value, ' ')[0];
        String callsignWithInvalidActivity = toLocationDeterminer.setTheirLocationFromWotaId(qso, wotaId.toUpperCase());
        qso.getTo().addActivity(activities.getDatabase(ActivityType.WOTA).get(wotaId));
        return new FieldParseResult(callsignWithInvalidActivity, true);
    }
}
