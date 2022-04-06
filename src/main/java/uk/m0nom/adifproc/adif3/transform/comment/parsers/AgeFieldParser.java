package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import uk.m0nom.adifproc.adif3.contacts.Qso;

public class AgeFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        try {
            int age = Integer.parseInt(value);
            if (age < 0) {
                throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, true, value);
            }
            qso.getRecord().setAge(age);
        } catch (NumberFormatException e) {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, e, true, value);
        }
        return FieldParseResult.SUCCESS;
    }
}
