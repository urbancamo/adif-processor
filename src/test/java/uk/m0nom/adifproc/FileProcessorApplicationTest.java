package uk.m0nom.adifproc;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.m0nom.FileProcessorApplication;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.adif3.Adif3Transformer;
import uk.m0nom.adifproc.adif3.io.Adif3FileReader;
import uk.m0nom.adifproc.adif3.io.Adif3FileWriter;
import uk.m0nom.adifproc.adif3.print.Adif3PrintFormatter;
import uk.m0nom.adifproc.kml.KmlWriter;
import uk.m0nom.adifproc.qrz.CachingQrzXmlService;


@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FileProcessorApplicationConfig.class)
public class FileProcessorApplicationTest
{
    @Autowired
    private Adif3Transformer transformer;

    @Autowired
    private Adif3FileReader reader;

    @Autowired
    private Adif3FileWriter writer;

    @Autowired
    private ActivityDatabaseService summits;

    @Autowired
    private Adif3PrintFormatter formatter;

    @Autowired
    private KmlWriter kmlWriter;

    @Autowired
    private CachingQrzXmlService qrzXmlService;

    @Test
    public void testApp() {
        String cli = "-k --encoding windows-1251 --qrz-username M0NOM --qrz-password WindermereIsMyQTH -l IO84MJ91MB -md -o ../ ./target/test-classes/adif/2021-07-08-Queen-Adelaides-Hill.adi";
        String[] args = cli.split(" ");

        FileProcessorApplication app = new FileProcessorApplication(transformer, reader, writer, summits, formatter, kmlWriter, qrzXmlService);
        app.run(args);
    }
}
