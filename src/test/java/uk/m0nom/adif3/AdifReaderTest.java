package uk.m0nom.adif3;

import com.amihaiemil.eoyaml.Yaml;
import com.amihaiemil.eoyaml.YamlMapping;
import org.junit.Test;
import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.adif3.transform.Adif3RecordTransformer;
import uk.m0nom.adif3.transform.CommentParsingAdifRecordTransformer;
import uk.m0nom.qrz.QrzXmlService;
import uk.m0nom.activity.ActivityDatabases;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;


public class AdifReaderTest {
    private final TransformControl control;

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
            assert firstRec != null;
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
            Qsos qsos = new Qsos(log);
            assertThat(log.getHeader().getProgramId()).isEqualTo("FLE");

            ActivityDatabases summits = new ActivityDatabases();
            summits.loadData();
            QrzXmlService qrzXmlService = new QrzXmlService(null, null);
            if (!qrzXmlService.getSessionKey()) {
                System.err.println("Could not connect to QRZ.COM, continuing...");
            }

            Adif3RecordTransformer transformer = new CommentParsingAdifRecordTransformer(config, summits, qrzXmlService, control);
            int index = 1;
            for (Adif3Record rec : log.getRecords()) {
                transformer.transform(qsos, rec, index++);
            }
            assertThat(log.getRecords()).hasSize(16);
        }
    }

    private BufferedReader resourceInput(String path) {
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream(path))));
    }
}
