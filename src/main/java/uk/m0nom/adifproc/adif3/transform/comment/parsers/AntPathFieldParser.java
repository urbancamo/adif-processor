package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.marsik.ham.adif.enums.AntPath;
import uk.m0nom.adifproc.adif3.contacts.Qso;

public class AntPathFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        try {
            AntPath path = AntPath.findByCode(value.toUpperCase());
            qso.getRecord().setAntPath(path);
        } catch (Exception e) {
            throw new CommentFieldParserException(this.getClass().getName(), "parseError", qso, false, value);
        }
        return FieldParseResult.SUCCESS;
    }
}
