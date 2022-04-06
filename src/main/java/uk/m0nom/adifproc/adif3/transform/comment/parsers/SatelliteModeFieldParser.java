package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import uk.m0nom.adifproc.adif3.contacts.Qso;

public class SatelliteModeFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        qso.getRecord().setSatMode(value);
        return FieldParseResult.SUCCESS;
    }
}
