package uk.m0nom.adifproc.adif3.transform.comment;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import com.google.common.base.Strings;
import org.marsik.ham.adif.Adif3Record;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.adif3.config.TransformerConfig;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.transform.TransformResults;
import uk.m0nom.adifproc.adif3.transform.comment.parsers.CommentFieldParser;
import uk.m0nom.adifproc.adif3.transform.comment.parsers.CommentFieldParserException;
import uk.m0nom.adifproc.adif3.transform.comment.parsers.CommentFieldParserFactory;
import uk.m0nom.adifproc.adif3.transform.comment.parsers.FieldParseResult;
import uk.m0nom.adifproc.adif3.transform.tokenizer.ColonTokenizer;
import uk.m0nom.adifproc.adif3.transform.tokenizer.CommentTokenizer;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.coords.LocationAccuracy;
import uk.m0nom.adifproc.coords.LocationSource;
import uk.m0nom.adifproc.errors.ErrorReporter;
import uk.m0nom.adifproc.maidenheadlocator.MaidenheadLocatorConversion;

import java.util.Map;
import java.util.logging.Logger;

@Service
public class FieldParserCommentTransformer implements CommentTransformer {
    private static final Logger logger = Logger.getLogger(FieldParserCommentTransformer.class.getName());
    private final CommentFieldParserFactory factory;
    private final CommentTokenizer tokenizer;
    private final TransformerConfig config;

    public FieldParserCommentTransformer(TransformerConfig config,
                                         CommentFieldParserFactory factory,
                                         ColonTokenizer tokenizer) {
        this.config = config;
        this.tokenizer = tokenizer;
        this.factory = factory;
    }

    @Override
    public void transformComment(Qso qso, String comment, Map<String, String> unmapped, TransformResults results) {
        if (Strings.isNullOrEmpty(comment)) {
            return;
        }

        Adif3Record rec = qso.getRecord();
        // try and split the comment up into comma separated list
        Map<String, String> tokens = tokenizer.tokenize(comment);

        // If this is a 'regular comment' then don't tokenize
        if (tokens.size() == 0) {
            unmapped.put(comment, "");
            return;
        }

        Double latitude = null;
        Double longitude = null;
        GlobalCoords3D coords = null;
        String callsignWithInvalidActivity = null;

        YamlMapping fieldMap = config.getConfig().asMapping();

        for (String key : tokens.keySet()) {
            String value = tokens.get(key).trim();

            YamlNode keyNode = fieldMap.value(key);
            if (keyNode != null) {
                YamlNode adifFieldYn = keyNode.asScalar();
                String adifField = adifFieldYn.asScalar().value();

                CommentFieldParser parser = factory.get(adifField);
                if (parser != null) {
                    try {
                        FieldParseResult result = parser.parseField(value, qso);
                        callsignWithInvalidActivity = result.getCallsign();

                        if (result.isAddToUnmapped()) {
                            unmapped.put(key, value);
                        }
                        if (result.getLatitude() != null) {
                            latitude = result.getLatitude();
                        }
                        if (result.getLongitude() != null) {
                            longitude = result.getLongitude();
                        }
                        if (result.getCoords() != null) {
                            coords = result.getCoords();
                        }
                    } catch (CommentFieldParserException exception) {
                        results.setError(ErrorReporter.formatError(exception.getClassName(), exception.getMessageKey(), exception.getArgs()));
                    }
                }
            }
            if (callsignWithInvalidActivity != null) {
                results.addContactWithDubiousLocation(callsignWithInvalidActivity);
            }
        }

        if (coords != null || (latitude != null && longitude != null)) {
        if (coords == null) {
            coords = new GlobalCoords3D(latitude, longitude, LocationSource.OVERRIDE, LocationAccuracy.LAT_LONG);
        }
        qso.getTo().setCoordinates(coords);
        rec.setCoordinates(coords);
        rec.setGridsquare(MaidenheadLocatorConversion.coordsToLocator(coords));
        logger.info(String.format("Override location of %s: %s", rec.getCall(), rec.getCoordinates().toString()));
    }

}
}
