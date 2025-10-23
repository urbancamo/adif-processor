package uk.m0nom.adifproc.adif3.transform.comment;

import org.assertj.core.data.Percentage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.marsik.ham.adif.Adif3Record;
import org.mockito.Mock;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.adif3.config.TransformerConfig;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.contacts.Station;
import uk.m0nom.adifproc.adif3.transform.TransformResults;
import uk.m0nom.adifproc.adif3.transform.tokenizer.ColonTokenizer;
import uk.m0nom.adifproc.adif3.xsdquery.Adif3SchemaLoader;
import uk.m0nom.adifproc.location.ToLocationDeterminer;
import uk.m0nom.adifproc.satellite.ApSatelliteService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

public class LatLonMigrateToCoordTest {

    private ClassicCommentTransformer classicTransformer;
    private SchemaBasedCommentTransformer schemaTransformer;

    @Mock
    private ActivityDatabaseService activities;
    @Mock
    private ToLocationDeterminer toLocationDeterminer;
    @Mock
    private ApSatelliteService apSatelliteService;

    private static final Percentage closeEnough = Percentage.withPercentage(0.00001);
    @BeforeEach
    public void setup() throws IOException {
        TransformerConfig config = new TransformerConfig();
        classicTransformer = new ClassicCommentTransformer(config, activities, toLocationDeterminer, apSatelliteService);
        ColonTokenizer tokenizer = new ColonTokenizer();
        Adif3SchemaLoader loader = new Adif3SchemaLoader();
        schemaTransformer = new SchemaBasedCommentTransformer(tokenizer, loader);
    }

    @Test
    public void testTransformLatLongComment() {
        Qso qso = new Qso();

        Adif3Record rec = new Adif3Record();
        rec.setCall("M7MCG/M");
        Station to = new Station();
        to.setCallsign(rec.getCall());
        qso.setTo(to);
        qso.setRecord(rec);

        TransformResults results = new TransformResults();
        Map<String, String> unmapped = new HashMap<>();

        String comment = "LAT: 53.92110358930624, LONG: -2.093893072210235";
        schemaTransformer.transformComment(qso, comment, unmapped, results);
        // Any unmapped comments we form into a new comment list
        String remainingComment = generateCommentFromUnmapped(unmapped);

        classicTransformer.transformComment(qso, remainingComment, unmapped, results);
        //assertThat(results.getWarnings()).isEmpty();
        assertThat(rec.getCoordinates().getLatitude()).isCloseTo(53.92110358930624, closeEnough);
        assertThat(rec.getCoordinates().getLongitude()).isCloseTo(-2.093893072210235, closeEnough);
    }

    private String generateCommentFromUnmapped(Map<String, String> unmapped) {
        StringBuilder sb = new StringBuilder();
        for (String key : unmapped.keySet()) {
            sb.append(String.format("%s: %s ", key, unmapped.get(key)));
        }
        return sb.toString();
    }
}
