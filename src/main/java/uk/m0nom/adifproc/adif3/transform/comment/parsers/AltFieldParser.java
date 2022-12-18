package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import uk.m0nom.adifproc.adif3.contacts.Qso;

public class AltFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        try {
            double alt = FieldParseUtils.parseAlt(value);
            if (alt < 0.0) {
                throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, true, value);
            }
            qso.getRecord().setAltitude(alt);
        } catch (NumberFormatException e) {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, e, true, value);
        }
        return FieldParseResult.SUCCESS;
    }
}
