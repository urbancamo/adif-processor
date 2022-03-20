package uk.m0nom.adif3.transform.comment.parsers;

import uk.m0nom.adif3.contacts.Qso;

public interface CommentFieldParser {
    FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException;
}

