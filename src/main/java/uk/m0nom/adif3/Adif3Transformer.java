package uk.m0nom.adif3;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.adif3.transform.Adif3RecordTransformer;
import uk.m0nom.adif3.transform.CommentParsingAdifRecordTransformer;
import uk.m0nom.qrz.QrzService;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class Adif3Transformer {
    private static final Logger logger = Logger.getLogger(Adif3Transformer.class.getName());

    private YamlMapping config = null;
    private ActivityDatabases summits;
    private QrzService qrzService;

    public void configure(InputStream yamlConfigFile, ActivityDatabases summits, QrzService qrzService)
            throws IOException {
        config = Yaml.createYamlInput(yamlConfigFile).readYamlMapping();
        this.summits = summits;
        this.qrzService = qrzService;
    }

    public Qsos transform(Adif3 log, TransformControl control) throws UnsupportedHeaderException {
        Adif3RecordTransformer transformer;
        Qsos qsos = new Qsos(log);

        transformer = new CommentParsingAdifRecordTransformer(config, summits, qrzService, control);
        int index = 1;
        for (Adif3Record rec : log.getRecords()) {
            if ((rec.getStationCallsign() != null || rec.getOperator() != null) && rec.getCall() != null) {
                transformer.transform(qsos, rec, index++);
            }
        }

        log.getHeader().setProgramId("M0NOM ADIF Processor");
        log.getHeader().setProgramVersion("1.0");
        return qsos;
    }
}
