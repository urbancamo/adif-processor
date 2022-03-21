package uk.m0nom.adif3.transform.comment.parsers;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.coords.LocationParserResult;
import uk.m0nom.coords.LocationParsers;
import uk.m0nom.coords.LocationSource;

public class CoordinatesFieldParser implements CommentFieldParser {
    private final LocationParsers locationParsers;

    public CoordinatesFieldParser(LocationParsers locationParsers) {
        this.locationParsers = locationParsers;
    }

    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        Adif3Record rec = qso.getRecord();
        LocationParserResult parserResult = locationParsers.parseStringForCoordinates(LocationSource.OVERRIDE, value);
        if (parserResult == null) {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, false, value, rec.getCall(), rec.getTimeOn().toString());
        }
        return new FieldParseResult(parserResult.getCoords());
    }
}
