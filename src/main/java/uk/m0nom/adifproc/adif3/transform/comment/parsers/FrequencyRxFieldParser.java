package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.adif3.contacts.Qso;

public class FrequencyRxFieldParser implements CommentFieldParser {

    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        Adif3Record rec = qso.getRecord();
        try {
            rec.setFreqRx(Double.parseDouble(value));
        } catch (NumberFormatException nfe) {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, nfe, true, value, rec.getCall(), rec.getTimeOn().toString());
        }
        return FieldParseResult.SUCCESS;
    }
}
