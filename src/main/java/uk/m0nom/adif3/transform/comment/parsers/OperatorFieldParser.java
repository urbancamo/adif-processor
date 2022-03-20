package uk.m0nom.adif3.transform.comment.parsers;

import uk.m0nom.adif3.contacts.Qso;

/**
 * A previous version of the ADIF Processor used OP as the keyword that populated the Name field.
 * In an attempt to maintain backwards compatibility this compromise implementation checks for at least one digit
 * in the passed value - if there is one or more digits the value is treated as a callsign and the Operator field
 * is populated. If not the value is set into the name field.
 */
public class OperatorFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) {
        if (value.matches("\\D*\\d+\\D*")) {
            qso.getRecord().setOperator(value);
        } else {
            qso.getRecord().setName(value);
        }
        return FieldParseResult.SUCCESS;
    }
}
