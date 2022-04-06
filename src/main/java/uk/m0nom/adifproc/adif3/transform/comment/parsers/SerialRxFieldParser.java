package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.adif3.contacts.Qso;

public class SerialRxFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        Adif3Record rec = qso.getRecord();
        // Determine if this is a serial number of string based contest exchange
        try {
            rec.setSrx(Integer.parseInt(value));
        } catch (NumberFormatException nfe) {
            // Not a simple number, so use the string ADIF field instead
            rec.setSrxString(value);
        }
        return FieldParseResult.SUCCESS;
    }
}
