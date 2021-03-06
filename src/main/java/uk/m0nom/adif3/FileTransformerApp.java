package uk.m0nom.adif3;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marsik.ham.adif.Adif3;
import uk.m0nom.adif3.args.CommandLineArgs;
import uk.m0nom.adif3.args.TransformControl;
import uk.m0nom.adif3.contacts.Qsos;
import uk.m0nom.adif3.print.Adif3PrintFormatter;
import uk.m0nom.adif3.transform.TransformResults;
import uk.m0nom.kml.KmlWriter;
import uk.m0nom.qrz.QrzXmlService;
import uk.m0nom.activity.ActivityDatabases;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class FileTransformerApp implements Runnable
{
    private static final String MARKDOWN_CONTROL_FILE = "adif-printer-132-markdown.yaml";
    private static final Logger logger = Logger.getLogger(FileTransformerApp.class.getName());

    private static FileTransformerApp instance;

    private CommandLineArgs cli;
    private Adif3Transformer transformer;
    private Adif3FileReaderWriter readerWriter;
    private KmlWriter kmlWriter;

    private ActivityDatabases summits;
    private QrzXmlService qrzXmlService;

    private Adif3PrintFormatter formatter;

    private Qsos qsos;

    private final static String configFilePath = "adif-processor.yaml";

    private final String[] args;

    static {
        InputStream stream = FileTransformerApp.class.getClassLoader().
                getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileTransformerApp(String[] args) {
        this.args = args;
        transformer = new Adif3Transformer();
        readerWriter = new Adif3FileReaderWriter();
        summits = new ActivityDatabases();
        cli = new CommandLineArgs();
        qsos = new Qsos();
        formatter = new Adif3PrintFormatter();
    }

    public static void main( String[] args )
    {
        instance = new FileTransformerApp(args);
        instance.run();
    }

    @Override
    public void run() {
        TransformResults results = new TransformResults();
        TransformControl control = cli.parseArgs(args);
        qrzXmlService = new QrzXmlService(control.getQrzUsername(), control.getQrzPassword());
        kmlWriter = new KmlWriter(control);


        String inPath = control.getPathname();
        String outPath = control.getOutputPath();
        // If we have an output path prepend that to the calculated output filenames
        if (StringUtils.isNotEmpty(outPath)) {
            if (!StringUtils.endsWith(outPath, File.separator) && !StringUtils.endsWith(outPath, "/")) {
                // ensure it ends in a path separator
                outPath = outPath + File.separator;
            }
        } else {
            outPath = FilenameUtils.getPath(inPath);
        }

        String inBasename = FilenameUtils.getBaseName(inPath);
        String out = String.format("%s%s-%s.%s", outPath, inBasename, "fta", "adi");
        String kml = String.format("%s%s-%s.%s", outPath, inBasename, "fta", "kml");
        String markdown = String.format("%s%s-%s.%s", outPath, inBasename, "fta", "md");
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

            logger.info(String.format("Reading input file %s with encoding %s", inPath, control.getEncoding()));
            Adif3 log = readerWriter.read(inPath, control.getEncoding(), false);
            qsos = transformer.transform(log, control);
            logger.info(String.format("Writing output file %s with encoding %s", out, control.getEncoding()));
            readerWriter.write(out, control.getEncoding(), log);
            if (control.getGenerateKml()) {
                kmlWriter.write(kml, inBasename, summits, qsos, results);
                if (results.getError() != null) {
                    kml = "";
                    logger.severe(results.getError());
                }
            }
            if (control.getMarkdown()) {
                BufferedWriter markdownWriter = null;
                try {
                    File markdownFile = new File(markdown);
                    if (markdownFile.exists()) {
                        if (!markdownFile.delete()) {
                            logger.severe(String.format("Error deleting Markdown file %s, check permissions?", markdown));
                        }
                    }
                    if (markdownFile.createNewFile()) {
                        formatter.getPrintJobConfig().configure(MARKDOWN_CONTROL_FILE);
                        logger.info(String.format("Writing Markdown to: %s", markdown));
                        StringBuilder sb = formatter.format(log);
                        markdownWriter = Files.newBufferedWriter(markdownFile.toPath(), Charset.forName(formatter.getPrintJobConfig().getOutEncoding()), StandardOpenOption.WRITE);
                        markdownWriter.write(sb.toString());
                    } else {
                        logger.severe(String.format("Error creating Markdown file %s, check permissions?", markdown));
                    }
                } catch (IOException ioe) {
                    logger.severe(String.format("Error writing Markdown file %s: %s", markdown, ioe.getMessage()));
                } finally {
                    if (markdownWriter != null) {
                        markdownWriter.close();
                    }
                }
            }
        } catch (NoSuchFileException nfe) {
            logger.severe(String.format("Could not open input file: %s", control.getPathname()));
        } catch (UnsupportedHeaderException ushe) {
            logger.severe(String.format("Unknown header for file: %s", inPath));
            logger.severe(ExceptionUtils.getStackTrace(ushe));
        } catch (IOException e) {
            logger.severe(String.format("Caught exception %s processing file: %s", e.getMessage(), inPath));
            logger.severe(ExceptionUtils.getStackTrace(e));
        }
    }
}
