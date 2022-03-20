package uk.m0nom.adif3.transform.comment;

import com.amihaiemil.eoyaml.YamlMapping;
import com.amihaiemil.eoyaml.YamlNode;
import com.google.common.base.Strings;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.transform.TransformResults;
import uk.m0nom.adif3.transform.comment.parsers.CommentFieldParser;
import uk.m0nom.adif3.transform.comment.parsers.CommentFieldParserException;
import uk.m0nom.adif3.transform.comment.parsers.CommentFieldParserFactory;
import uk.m0nom.adif3.transform.comment.parsers.FieldParseResult;
import uk.m0nom.adif3.transform.tokenizer.ColonTokenizer;
import uk.m0nom.adif3.transform.tokenizer.CommentTokenizer;
import uk.m0nom.coords.GlobalCoords3D;
import uk.m0nom.coords.LocationAccuracy;
import uk.m0nom.coords.LocationParsers;
import uk.m0nom.coords.LocationSource;
import uk.m0nom.errors.ErrorReporter;
import uk.m0nom.location.ToLocationDeterminer;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;
import uk.m0nom.satellite.ApSatellites;

import java.util.Map;
import java.util.logging.Logger;

public class FieldParserCommentTransformer implements CommentTransformer {
    private static final Logger logger = Logger.getLogger(FieldParserCommentTransformer.class.getName());
    private final CommentFieldParserFactory factory;
    private final CommentTokenizer tokenizer;
    private final YamlMapping fieldMap;

    public FieldParserCommentTransformer(YamlMapping config,
                                     ActivityDatabases activities,
                                     ToLocationDeterminer toLocationDeterminer,
                                     ApSatellites apSatellites) {
        tokenizer = new ColonTokenizer();
        fieldMap = config.asMapping();
        LocationParsers locationParsers = new LocationParsers();
        factory = new CommentFieldParserFactory(activities, toLocationDeterminer,locationParsers,apSatellites);
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
