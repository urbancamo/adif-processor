package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.types.Wwff;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityDatabase;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.activity.wwff.WwffInfo;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.contacts.Station;
import uk.m0nom.adifproc.location.ToLocationDeterminer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class WwffFieldParserTest {

    @InjectMocks
    private WwffFieldParser wwffFieldParser;

    private Qso qso;

    private Adif3Record rec;

    @BeforeEach
    void init(@Mock ToLocationDeterminer toLocationDeterminer,
              @Mock ActivityDatabaseService activityDatabaseService) {

        rec = new Adif3Record();
        qso = new Qso();
        Station to = new Station();

        Activity svff0185Activity = new WwffInfo();
        svff0185Activity.setRef("SVFF-0185");
        svff0185Activity.setName("Name");
        qso.setRecord(rec);
        qso.setTo(to);
        wwffFieldParser = new WwffFieldParser(toLocationDeterminer, activityDatabaseService);
    }


    @Test
    public void testWwffAdifFieldPopulatedCorrect() throws CommentFieldParserException {
        Wwff wwff = Wwff.valueOf("SVFF-0185");
        FieldParseResult fpr = wwffFieldParser.parseField("SVFF-0185", qso);
        assertThat(fpr.isAddToUnmapped()).isFalse();
        assertThat(rec.getWwffRef().equals(wwff)).isTrue();
    }
}
