package uk.m0nom.adif3;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import org.junit.Test;
import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.qrz.QrzXmlService;
import uk.m0nom.sota.SotaCsvReader;
import uk.m0nom.summits.SummitsDatabase;
import uk.m0nom.wota.WotaCsvReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class AdifReaderTest {
    private SummitsDatabase summits;
    private TransformControl control;

    public AdifReaderTest() {
        control = new TransformControl();
    }

    @Test
    public void testAdifLOGHX() throws Exception {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader = resourceInput("adif/2020-12-14-test-from-loghx-shuttle1.adi");
        Optional<Adif3> result = reader.read(inputReader);
        if (result.isPresent()) {
            Adif3 log = result.get();
            assertThat(log.getHeader().getProgramId()).isEqualTo("LOGHX");
            Adif3Record firstRec = null;
            assertThat(log.getRecords()).hasSize(1);
            for (Adif3Record rec : log.getRecords()) {
                if (firstRec == null) {
                    firstRec = rec;
                }
            }
            assertThat(firstRec.getCall()).isEqualTo("M6VMS");
        }
    }

    @Test
    public void testAdifFLE() throws Exception {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader = resourceInput("adif/2021-01-09-Queen-Adelaides-Hill.adi");
        Optional<Adif3> result = reader.read(inputReader);
        //System.out.println(new File("tmp.txt").getAbsolutePath());
        YamlMapping config = Yaml.createYamlInput(new File("adif-processor.yaml")).readYamlMapping();
        if (result.isPresent()) {
            Adif3 log = result.get();
            assertThat(log.getHeader().getProgramId()).isEqualTo("FLE");

            summits = new SummitsDatabase();
            summits.loadData();
            QrzXmlService qrzXmlService = new QrzXmlService(null, null);
            if (!qrzXmlService.getSessionKey()) {
                System.err.println("Could not connect to QRZ.COM, continuing...");
            }

            Adif3RecordTransformer transformer = new FastLogEntryAdifRecordTransformer(config, summits, qrzXmlService, control);
            for (Adif3Record rec : log.getRecords()) {
                transformer.transform(rec);
            }
            assertThat(log.getRecords()).hasSize(16);
        }
    }

    private BufferedReader resourceInput(String path) {
        return new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path)));
    }
}
