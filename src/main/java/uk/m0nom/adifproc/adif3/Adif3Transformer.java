package uk.m0nom.adifproc.adif3;

import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.AdifHeader;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.adif3.contacts.Qsos;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.adif3.transform.CommentParsingAdifRecordTransformer;
import uk.m0nom.adifproc.adif3.transform.TransformResults;

/**
 * Main entry into the Adif3 Transformer functionality.
 */
@Service
public class Adif3Transformer {

    private final CommentParsingAdifRecordTransformer transformer;

    public Adif3Transformer(CommentParsingAdifRecordTransformer transformer) {
        this.transformer = transformer;
    }

    public Qsos transform(Adif3 log, TransformControl control, TransformResults results) throws UnsupportedHeaderException {
        Qsos qsos = new Qsos(log);

        int index = 1;
        boolean myCallsignIssue = false;
        boolean theirCallsignIssue = false;
        for (Adif3Record rec : log.getRecords()) {
            boolean haveMyCallsign = rec.getStationCallsign() != null || rec.getOperator() != null;
            boolean haveTheirCallsign = rec.getCall() != null;
            if (haveMyCallsign && haveTheirCallsign) {
                transformer.transform(control, results, qsos, rec, index++);
            } else {
                myCallsignIssue |= haveMyCallsign;
                theirCallsignIssue |= haveTheirCallsign;
            }
        }

        if (theirCallsignIssue) {
            results.setError("Check you have CALLSIGN or OPERATOR defined for every record");
        }
        else if (myCallsignIssue) {
            results.setError("Check you have MY_CALLSIGN or MY_OPERATOR defined for every record");
        }

        AdifHeader header = new AdifHeader();
        header.setProgramId("M0NOM ADIF Processor");
        header.setProgramVersion("1.0");
        log.setHeader(header);

        return qsos;
    }
}
