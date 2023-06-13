package uk.m0nom.adifproc.adif3.transform.comment;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.transform.TransformResults;
import uk.m0nom.adifproc.adif3.transform.tokenizer.ColonTokenizer;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class SchemaBasedCommentTransformerTest {

    private static SchemaBasedCommentTransformer transformer;

    @BeforeAll
    public static void setup() {
        ColonTokenizer tokenizer = new ColonTokenizer();
        transformer = new SchemaBasedCommentTransformer(tokenizer);
    }

    @Test
    public void testTransformAltitudeComment() {
        Qso qso = new Qso();
        Adif3Record rec = new Adif3Record();
        qso.setRecord(rec);

        TransformResults results = new TransformResults();
        Map<String, String> unmapped = new HashMap<>();

        String comment = "altitude: 1000";
        transformer.transformComment(qso, comment, unmapped, results);
        assertThat(results.getWarnings()).isEmpty();
        assertThat(rec.getAltitude()).isEqualTo(1000.0);

        comment= "altitude: -1";
        transformer.transformComment(qso, comment, unmapped, results);
        assertThat(results.getWarnings()).isEmpty();
        assertThat(rec.getAltitude()).isEqualTo(-1);

        comment = "ant_az: 450";
        transformer.transformComment(qso, comment, unmapped, results);
        assertThat(results.getWarnings()).contains("Validation of comment field 'ANT_AZ:450' failed because value is too high");
        assertThat(unmapped.containsKey("ANT_AZ")).isTrue();
        assertThat(unmapped.containsValue("450")).isTrue();

        comment = "POTA_REF: EA-2120,EA-0825,EA-0050 WWFF: EAFF-0265";
        transformer.transformComment(qso, comment, unmapped, results);
    }

}
