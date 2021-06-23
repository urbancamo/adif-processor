package uk.m0nom.adif3;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marsik.ham.adif.Adif3;
import uk.m0nom.adif3.args.CommandLineArgs;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.kml.KmlWriter;
import uk.m0nom.qrz.QrzXmlService;
import uk.m0nom.sota.SotaCsvReader;
import uk.m0nom.summits.SummitsDatabase;
import uk.m0nom.wota.WotaCsvReader;

import java.io.File;
import java.io.IOException;
import java.nio.file.NoSuchFileException;
import java.util.logging.Logger;

public class FileTransformerApp implements Runnable
{
    private static final Logger logger = Logger.getLogger(FileTransformerApp.class.getName());

    private static FileTransformerApp instance;

    private CommandLineArgs cli;
    private Adif3Transformer transformer;
    private Adif3FileReaderWriter readerWriter;
    private KmlWriter kmlWriter;

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
        kmlWriter = new KmlWriter();
        cli = new CommandLineArgs();
    }

    public static void main( String[] args )
    {
        instance = new FileTransformerApp(args);
        instance.run();
    }

    @Override
    public void run() {
        TransformControl control = cli.parseArgs(args);
        String in = control.getPathname();
        String out = String.format("%s-%s.%s", FilenameUtils.removeExtension(in.toString()), "fta", "adi");
        String kml = String.format("%s-%s.%s", FilenameUtils.removeExtension(in.toString()), "fta", "kml");

        logger.info(String.format("Running from: %s", new File(".").getAbsolutePath()));
        try {
            summits.loadData();
            if (control.getUseQrzDotCom()) {
                qrzXmlService.enable();
                if (!qrzXmlService.getSessionKey()) {
                    logger.warning("Could not connect to QRZ.COM, disabling lookups and continuing...");
                    qrzXmlService.disable();
                }
            }
            transformer.configure(configFilePath, summits, qrzXmlService);

            logger.info(String.format("Reading input file %s with encoding %s", control.getPathname(), control.getEncoding()));
            Adif3 log = readerWriter.read(in, control.getEncoding(), false);
            transformer.transform(log, control);
            readerWriter.write(out, control.getEncoding(), log);
            if (control.getGenerateKml()) {
                kmlWriter.write(kml, log);
            }
        } catch (NoSuchFileException nfe) {
            logger.severe(String.format("Could not open input file: %s", control.getPathname()));
        } catch (UnsupportedHeaderException ushe) {
            logger.severe(String.format("Unknown header for file: %s", in));
            logger.severe(ExceptionUtils.getStackTrace(ushe));
        } catch (IOException e) {
            logger.severe(String.format("Caught exception %s processing file: %s", e.getMessage(), in));
            logger.severe(ExceptionUtils.getStackTrace(e));
        }
    }
}
