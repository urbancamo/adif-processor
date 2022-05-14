package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.transform.ApplicationDefinedFields;

public class AntFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        qso.getRecord().addApplicationDefinedField(ApplicationDefinedFields.ANT, value);
        return FieldParseResult.SUCCESS;
    }}
