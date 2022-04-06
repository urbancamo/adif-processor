package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.QslSent;
import org.marsik.ham.adif.enums.QslVia;
import uk.m0nom.adifproc.adif3.contacts.Qso;

import java.time.LocalDate;

public class QslFieldParser implements CommentFieldParser {
    @Override
    public FieldParseResult parseField(String value, Qso qso) throws CommentFieldParserException {
        Adif3Record rec = qso.getRecord();

        rec.setQslSDate(LocalDate.now());
        rec.setQslSent(QslSent.SENT);
        // This could either be a bureau or direct QSL depending on value
        switch (value) {
            case "D":
                rec.setQslSentVia(QslVia.DIRECT);
                break;
            case "B":
                rec.setQslSentVia(QslVia.BUREAU);
                break;
        }

        return FieldParseResult.SUCCESS;
    }
}
