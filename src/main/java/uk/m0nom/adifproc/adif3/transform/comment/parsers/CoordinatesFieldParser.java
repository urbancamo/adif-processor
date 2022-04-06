package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.coords.LocationParserResult;
import uk.m0nom.adifproc.coords.LocationParsingService;
import uk.m0nom.adifproc.coords.LocationSource;

public class CoordinatesFieldParser implements CommentFieldParser {
    private final LocationParsingService locationParsingService;

    public CoordinatesFieldParser(LocationParsingService locationParsingService) {
        this.locationParsingService = locationParsingService;
    }

    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        Adif3Record rec = qso.getRecord();
        LocationParserResult parserResult = locationParsingService.parseStringForCoordinates(LocationSource.OVERRIDE, value);
        if (parserResult == null) {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, false, value, rec.getCall(), rec.getTimeOn().toString());
        }
        return new FieldParseResult(parserResult.getCoords());
    }
}
