package uk.m0nom.adif3;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.qrz.QrzXmlService;
import uk.m0nom.activity.ActivityDatabase;

import java.io.*;

public class Adif3Transformer {
    private YamlMapping config = null;
    private ActivityDatabase summits;
    private QrzXmlService qrzXmlService;

    public void configure(String yamlConfigFile, ActivityDatabase summits, QrzXmlService qrzXmlService) throws IOException {
        config = Yaml.createYamlInput(new File(yamlConfigFile)).readYamlMapping();
        this.summits = summits;
        this.qrzXmlService = qrzXmlService;
    }

    public Qsos transform(Adif3 log, TransformControl control) throws UnsupportedHeaderException {
        Adif3RecordTransformer transformer;
        Qsos qsos = new Qsos(log);

        transformer = new FastLogEntryAdifRecordTransformer(config, summits, qrzXmlService, control);
        for (Adif3Record rec : log.getRecords()) {
            transformer.transform(qsos, rec);
        }
        return qsos;
    }
}
