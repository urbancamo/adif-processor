package uk.m0nom.adifproc.adif3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.m0nom.adifproc.FileProcessorApplication;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.adif3.io.Adif3FileReader;
import uk.m0nom.adifproc.adif3.io.Adif3FileWriter;
import uk.m0nom.adifproc.adif3.print.Adif3PrintFormatter;
import uk.m0nom.adifproc.kml.KmlWriter;
import uk.m0nom.adifproc.qrz.CachingQrzXmlService;

@SpringBootTest
@ActiveProfiles("test")
public class BadGridTest
{
    @Autowired
    Adif3Transformer transformer;

    @Autowired
    Adif3FileReader reader;

    @Autowired
    Adif3FileWriter writer;

    @Autowired
    ActivityDatabaseService summits;

    @Autowired
    Adif3PrintFormatter formatter;

    @Autowired
    KmlWriter kmlWriter;

    @Autowired
    CachingQrzXmlService qrzXmlService;

    @Test
    public void testApp() {
        try {
            String cli = "-k --encoding windows-1251 -md -o ../ ./target/test-classes/adif/2021-08-23-ADIF-with-bad-gridsquare.adi";
            String[] args = cli.split(" ");

            FileProcessorApplication app = new FileProcessorApplication(transformer, reader, writer, summits, formatter, kmlWriter, qrzXmlService);
            app.run(args);
        } catch (Exception e) {
            Assertions.fail("testData() threw exception while setting up the test. ", e);
        }
    }
}
