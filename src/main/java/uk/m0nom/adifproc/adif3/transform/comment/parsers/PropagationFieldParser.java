package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adifproc.adif3.contacts.Qso;

public class PropagationFieldParser implements CommentFieldParser {

    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        try {
            Propagation mode = Propagation.findByCode(value.toUpperCase());
            qso.getRecord().setPropMode(mode);
        } catch (Exception e) {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, false, value);
        }
        return FieldParseResult.SUCCESS;
    }
}
