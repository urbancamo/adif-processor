package uk.m0nom.adifproc.adif3;

import org.apache.commons.lang.StringUtils;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.AdifHeader;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.adif3.contacts.Qsos;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.adif3.transform.CommentParsingAdifRecordTransformer;
import uk.m0nom.adifproc.adif3.transform.MyCallsignCheck;
import uk.m0nom.adifproc.adif3.transform.MyCallsignCheckResults;
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
        int myCallsignIssues = 0;
        int theirCallsignIssues = 0;

        MyCallsignCheckResults callsigns = MyCallsignCheck.checkForSingleMyCallsign(log);

        for (Adif3Record rec : log.getRecords()) {
            if (StringUtils.isBlank(rec.getOperator()) && callsigns.isOneOperator()) {
                rec.setOperator(callsigns.getSingleOperator());
            }
            if (StringUtils.isBlank(rec.getStationCallsign()) && callsigns.isOneStationCallsign()) {
                rec.setStationCallsign(callsigns.getSingleStationCallsign());
            }

            boolean haveMyCallsign = rec.getStationCallsign() != null || rec.getOperator() != null;
            boolean haveTheirCallsign = rec.getCall() != null;
            if (haveMyCallsign && haveTheirCallsign) {
                transformer.transform(control, results, qsos, rec, index);
            } else {
                if (!haveMyCallsign) {
                    myCallsignIssues++;
                    if (firstError == 0) {
                        firstError = index;
                        additionalInfo = String.format("record %d%s", firstError, StringUtils.defaultIfEmpty(String.format(", their call: %s", rec.getCall()), ""));
                    }
                }
                if (!haveTheirCallsign) {
                    theirCallsignIssues++;
                    if (firstError == 0) {
                        firstError = index;
                        additionalInfo = String.format("record %d", firstError);
                    }
                }
            }
            index++;
        }

        if (theirCallsignIssues > 0) {
            results.setError(String.format("CALL not defined for %d record(s), first error on %s", theirCallsignIssues, additionalInfo));
        }
        else if (myCallsignIssues > 0) {
            results.setError(String.format("STATION_CALLSIGN or OPERATOR not defined for %d record(s), first error on %s", myCallsignIssues, additionalInfo));
        }

        AdifHeader header = new AdifHeader();
        header.setProgramId("M0NOM ADIF Processor");
        header.setProgramVersion("1.0");
        log.setHeader(header);

        return qsos;
    }
}
