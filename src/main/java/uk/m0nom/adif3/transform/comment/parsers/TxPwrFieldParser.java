package uk.m0nom.adif3.transform.comment.parsers;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.contacts.Qso;

public class TxPwrFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        Adif3Record rec = qso.getRecord();
        try {
            rec.setTxPwr(FieldParseUtils.parsePwr(value));
        } catch (NumberFormatException nfe) {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, nfe, true, value);
        }
        return FieldParseResult.SUCCESS;
    }
}
