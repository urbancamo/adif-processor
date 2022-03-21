package uk.m0nom.adif3.transform.comment.parsers;

import uk.m0nom.adif3.contacts.Qso;

public class RigFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) {
        qso.getRecord().setRig(value);
        return FieldParseResult.SUCCESS;
    }
}
