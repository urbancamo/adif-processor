package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import uk.m0nom.adifproc.adif3.contacts.Qso;

public class WebFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        try {
            qso.getRecord().setWeb(value);
        } catch (Exception e) {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, false, value);
        }
        return FieldParseResult.SUCCESS;
    }
}
