package uk.m0nom.adif3.transform.comment.parsers;

import uk.m0nom.adif3.contacts.Qso;

import java.util.logging.Logger;

public class RxPwrFieldParser implements CommentFieldParser {
    private static final Logger logger = Logger.getLogger(RxPwrFieldParser.class.getName());

    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        try {
            qso.getRecord().setRxPwr(FieldParseUtils.parsePwr(value));
        } catch (NumberFormatException nfe) {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, nfe, true, value);
        }
        return FieldParseResult.SUCCESS;
    }
}
