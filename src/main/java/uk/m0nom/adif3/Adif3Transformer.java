package uk.m0nom.adif3;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.AdiWriter;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.qrz.QrzXmlService;
import uk.m0nom.summits.SummitsDatabase;

import java.io.*;
import java.util.Optional;

public class Adif3Transformer {
    private YamlMapping config = null;
    private SummitsDatabase summits;
    private QrzXmlService qrzXmlService;

    public void configure(String yamlConfigFile, SummitsDatabase summits, QrzXmlService qrzXmlService) throws IOException {
        config = Yaml.createYamlInput(new File(yamlConfigFile)).readYamlMapping();
        this.summits = summits;
        this.qrzXmlService = qrzXmlService;
    }

    public void transform(Adif3 log) throws UnsupportedHeaderException {
        Adif3RecordTransformer transformer = null;

        if (log.getHeader() != null) {
            switch (log.getHeader().getProgramId()) {
                case "FLE":
                    transformer = new FastLogEntryAdifRecordTransformer(config, summits, qrzXmlService);
                    break;
                case "LOGHX":
                    transformer = new LogHXAdifRecordTransformer();
                    break;
                default:
                    throw new UnsupportedHeaderException();
            }
            if (transformer != null) {
                for (Adif3Record rec : log.getRecords()) {
                    transformer.transform(rec);
                }
            }
        } else {
            throw new UnsupportedHeaderException();
        }
    }
}
