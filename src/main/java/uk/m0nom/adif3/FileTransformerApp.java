package uk.m0nom.adif3;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marsik.ham.adif.Adif3;
import uk.m0nom.qrz.QrzXmlService;
import uk.m0nom.sota.SotaCsvReader;
import uk.m0nom.summits.SummitsDatabase;
import uk.m0nom.wota.WotaCsvReader;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

public class FileTransformerApp implements Runnable
{
    private static final Logger logger = Logger.getLogger(FileTransformerApp.class.getName());

    private static FileTransformerApp instance;

    private Adif3Transformer transformer;
    private Adif3FileReaderWriter readerWriter;

    private SummitsDatabase summits;
    private QrzXmlService qrzXmlService;

    private final static String configFilePath = "adif-processor.yaml";

    private String args[];

    public FileTransformerApp(String args[]) {
        this.args = args;
        transformer = new Adif3Transformer();
        readerWriter = new Adif3FileReaderWriter();
        summits = new SummitsDatabase();
        qrzXmlService = new QrzXmlService();
    }

    public static void main( String[] args )
    {
        instance = new FileTransformerApp(args);
        instance.run();
    }

    @Override
    public void run() {
        if (args.length < 1 || args.length > 2) {
            logger.config(String.format("Usage: %s <inputFile>.adi [<outputFile>.adi]", this.getClass().getName()));
        } else {
            String in = args[0];
            String out = String.format("%s-%s.%s", FilenameUtils.removeExtension(in.toString()), "fta", "adi");
            if (args.length == 2) {
                out = args[1];
            }

            logger.info(String.format("Running from: %s", new File(".").getAbsolutePath()));
            try {
                summits.loadData();
                if (!qrzXmlService.getSessionKey() && !qrzXmlService.isDisabled()) {
                    logger.warning("Could not connect to QRZ.COM, continuing...");
                }
                transformer.configure(configFilePath, summits, qrzXmlService);

                Adif3 log = readerWriter.read(in, "windows-1252", false);
                transformer.transform(log);
                readerWriter.write(out, log);
            } catch (UnsupportedHeaderException ushe) {
                logger.severe(String.format("Unknown header for file: %s", in));
                logger.severe(ExceptionUtils.getStackTrace(ushe));
            } catch (IOException e) {
                logger.severe(String.format("Caught exception %s processing file: %s", e.getMessage(), in));
                logger.severe(ExceptionUtils.getStackTrace(e));
            }
        }
    }
}
