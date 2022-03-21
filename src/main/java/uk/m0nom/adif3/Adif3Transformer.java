package uk.m0nom.adif3;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.AdifHeader;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.adif3.transform.Adif3RecordTransformer;
import uk.m0nom.adif3.transform.CommentParsingAdifRecordTransformer;
import uk.m0nom.adif3.transform.TransformResults;
import uk.m0nom.qrz.QrzService;

import java.io.IOException;
import java.io.InputStream;

/**
 * Main entry into the Adif3 Transformer functionality.
 * Current set to use the:
 * 
 */
public class Adif3Transformer {

    private YamlMapping config = null;
    private ActivityDatabases summits;
    private QrzService qrzService;

    public void configure(InputStream yamlConfigFile, ActivityDatabases summits, QrzService qrzService)
            throws IOException {
        config = Yaml.createYamlInput(yamlConfigFile).readYamlMapping();
        this.summits = summits;
        this.qrzService = qrzService;
    }

    public Qsos transform(Adif3 log, TransformControl control, TransformResults results) throws UnsupportedHeaderException {
        Adif3RecordTransformer transformer;
        Qsos qsos = new Qsos(log);

        transformer = new CommentParsingAdifRecordTransformer(config, summits, qrzService, control, results);
        int index = 1;
        boolean myCallsignIssue = false;
        boolean theirCallsignIssue = false;
        for (Adif3Record rec : log.getRecords()) {
            boolean haveMyCallsign = rec.getStationCallsign() != null || rec.getOperator() != null;
            boolean haveTheirCallsign = rec.getCall() != null;
            if (haveMyCallsign && haveTheirCallsign) {
                transformer.transform(qsos, rec, index++);
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
