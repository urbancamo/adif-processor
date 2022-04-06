package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.coords.LatLongUtils;

public class LatitudeParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        Adif3Record rec = qso.getRecord();

        double latitude;
        try {
            latitude = Double.parseDouble(value);
            if (!LatLongUtils.checkLatitudeRange(latitude)) {
                throw new CommentFieldParserException(this.getClass().getName(), "rangeError", qso, false, rec.getCall(), rec.getTimeOn().toString(), value);
            }
        } catch (NumberFormatException e) {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, false, rec.getCall(), rec.getTimeOn().toString(), value);
        }
        return new FieldParseResult(latitude, null);
    }
}
