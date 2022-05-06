package uk.m0nom.adifproc.adif3;

import org.apache.commons.lang.StringUtils;
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

        int firstError = 0;
        int index = 1;
        String additionalInfo = "";
        boolean myCallsignIssue = false;
        boolean theirCallsignIssue = false;
        for (Adif3Record rec : log.getRecords()) {
            boolean haveMyCallsign = rec.getStationCallsign() != null || rec.getOperator() != null;
            boolean haveTheirCallsign = rec.getCall() != null;
            if (haveMyCallsign && haveTheirCallsign) {
                transformer.transform(control, results, qsos, rec, index);
            } else {
                if (!haveMyCallsign) {
                    myCallsignIssue = true;
                    if (firstError == 0) {
                        firstError = index;
                        additionalInfo = String.format("record %d %s", firstError, StringUtils.defaultIfEmpty(String.format(", their call: %s", rec.getCall()), ""));
                    }
                }
                if (!haveTheirCallsign) {
                    theirCallsignIssue = true;
                    if (firstError == 0) {
                        firstError = index;
                        additionalInfo = String.format("record %d", firstError);
                    }
                }
            }
            index++;
        }

        if (theirCallsignIssue) {
            results.setError(String.format("CALL not defined for every record, first error on %s", additionalInfo));
        }
        else if (myCallsignIssue) {
            results.setError(String.format("STATION_CALLSIGN or OPERATOR not defined for every record, first error on %s", additionalInfo));
        }

        AdifHeader header = new AdifHeader();
        header.setProgramId("M0NOM ADIF Processor");
        header.setProgramVersion("1.0");
        log.setHeader(header);

        return qsos;
    }
}
