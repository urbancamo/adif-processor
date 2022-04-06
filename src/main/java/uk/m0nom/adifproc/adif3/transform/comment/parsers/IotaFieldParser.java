package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.types.Iota;
import uk.m0nom.adifproc.adif3.contacts.Qso;

public class IotaFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        Adif3Record rec = qso.getRecord();
        try {
            Iota iota = Iota.findByCode(value);
            rec.setIota(iota);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, e, true, value, rec.getCall(), rec.getTimeOn().toString());
        }
        return FieldParseResult.SUCCESS;
    }
}
