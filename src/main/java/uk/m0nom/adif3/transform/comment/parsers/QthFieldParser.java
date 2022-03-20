package uk.m0nom.adif3.transform.comment.parsers;

import uk.m0nom.adif3.contacts.Qso;

public class QthFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) {
        qso.getRecord().setQth(value);
        return FieldParseResult.SUCCESS;
    }
}
