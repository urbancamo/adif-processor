package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.transform.ApplicationDefinedFields;

public class MyAltFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        try {
            double alt = FieldParseUtils.parseAlt(value);
            if (alt < 0.0) {
                throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, true, value);
            }
            qso.getRecord().addApplicationDefinedField(ApplicationDefinedFields.MY_ALT, value);
       } catch (NumberFormatException e) {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, e, true, value);
        }
        return FieldParseResult.SUCCESS;
    }
}
