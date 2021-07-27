package uk.m0nom.adif3;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.adif3.transform.Adif3RecordTransformer;
import uk.m0nom.adif3.transform.CommentParsingAdifRecordTransformer;
import uk.m0nom.qrz.QrzXmlService;
import uk.m0nom.activity.ActivityDatabases;

import java.io.*;
import java.util.logging.Logger;

public class Adif3Transformer {
    private static final Logger logger = Logger.getLogger(Adif3Transformer.class.getName());

    private YamlMapping config = null;
    private ActivityDatabases summits;
    private QrzXmlService qrzXmlService;

    public void configure(InputStream yamlConfigFile, ActivityDatabases summits, QrzXmlService qrzXmlService) throws IOException {
        config = Yaml.createYamlInput(yamlConfigFile).readYamlMapping();
        this.summits = summits;
        this.qrzXmlService = qrzXmlService;
    }

    public Qsos transform(Adif3 log, TransformControl control) throws UnsupportedHeaderException {
        Adif3RecordTransformer transformer;
        Qsos qsos = new Qsos(log);

        transformer = new CommentParsingAdifRecordTransformer(config, summits, qrzXmlService, control);
        for (Adif3Record rec : log.getRecords()) {
            transformer.transform(qsos, rec);
        }

        // Change the header
        log.getHeader().setProgramId("M0NOM ADIF Transformer");
        log.getHeader().setProgramVersion("1.0");
        return qsos;
    }
}
