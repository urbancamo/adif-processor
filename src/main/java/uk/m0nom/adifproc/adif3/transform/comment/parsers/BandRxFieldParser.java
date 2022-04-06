package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Band;
import uk.m0nom.adifproc.adif3.contacts.Qso;

import java.util.logging.Logger;

public class BandRxFieldParser implements CommentFieldParser {
    private static final Logger logger = Logger.getLogger(BandRxFieldParser.class.getName());

    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        Adif3Record rec = qso.getRecord();
        try {
            Band band = Band.findByCode(value);
            rec.setBandRx(band);
        } catch (ArrayIndexOutOfBoundsException e) {
            throw new CommentFieldParserException(this.getClass().getName(),  "parseError", qso, e, true, value, rec.getCall(), rec.getTimeOn().toString());
        }
        return FieldParseResult.SUCCESS;
    }
}
