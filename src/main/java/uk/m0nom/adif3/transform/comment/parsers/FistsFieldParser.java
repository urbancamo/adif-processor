package uk.m0nom.adif3.transform.comment.parsers;

import uk.m0nom.adif3.contacts.Qso;

public class FistsFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        qso.getRecord().setFists(value);
        return FieldParseResult.SUCCESS;
    }
}
