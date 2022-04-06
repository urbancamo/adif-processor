package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import uk.m0nom.adifproc.adif3.contacts.Qso;

public class SkccFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        qso.getRecord().setSkcc(value);

        return FieldParseResult.SUCCESS;
    }
}
