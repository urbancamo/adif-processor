package uk.m0nom;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marsik.ham.adif.Adif3;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.adif3.Adif3Transformer;
import uk.m0nom.adifproc.adif3.UnsupportedHeaderException;
import uk.m0nom.adifproc.adif3.args.CommandLineArgs;
import uk.m0nom.adifproc.adif3.contacts.Qsos;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.adif3.io.Adif3FileReader;
import uk.m0nom.adifproc.adif3.io.Adif3FileWriter;
import uk.m0nom.adifproc.adif3.print.Adif3PrintFormatter;
import uk.m0nom.adifproc.adif3.transform.TransformResults;
import uk.m0nom.adifproc.contest.ContestResultsCalculator;
import uk.m0nom.adifproc.dxcc.DxccEntities;
import uk.m0nom.adifproc.dxcc.DxccJsonReader;
import uk.m0nom.adifproc.dxcc.JsonDxccEntities;
import uk.m0nom.adifproc.kml.KmlWriter;
import uk.m0nom.adifproc.qrz.CachingQrzXmlService;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.StandardOpenOption;
import java.text.ParseException;
import java.util.Objects;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@SpringBootApplication
public class FileProcessorApplication implements CommandLineRunner {
    private static final String MARKDOWN_CONTROL_FILE = "adif-printer-132-md.yaml";

    private static final Logger logger = Logger.getLogger(FileProcessorApplication.class.getName());

    private final CommandLineArgs cli;
    private final Adif3Transformer transformer;
    private final Adif3FileReader reader;
    private final Adif3FileWriter writer;

    private final ActivityDatabaseService summits;

    private final Adif3PrintFormatter formatter;
    private final KmlWriter kmlWriter;
    private final CachingQrzXmlService qrzXmlService;

    private Qsos qsos;

    static {
        InputStream stream = FileProcessorApplication.class.getClassLoader().
                getResourceAsStream("logging.properties");
        try {
            LogManager.getLogManager().readConfiguration(stream);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                Objects.requireNonNull(stream).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public FileProcessorApplication(Adif3Transformer transformer, Adif3FileReader reader, Adif3FileWriter writer,
                                    ActivityDatabaseService summits, Adif3PrintFormatter formatter, KmlWriter kmlWriter,
                                    CachingQrzXmlService qrzXmlService) {
        this.transformer = transformer;
        this.reader = reader;
        this.writer = writer;
        this.summits = summits;
        this.formatter = formatter;
        this.kmlWriter = kmlWriter;
        this.qrzXmlService = qrzXmlService;

        cli = new CommandLineArgs();
        qsos = new Qsos();
    }

    public static void main(String[] args) {
        logger.info("STARTING THE APPLICATION");
        SpringApplication.run(FileProcessorApplication.class, args);
        logger.info("APPLICATION FINISHED");
    }

    @Override
    public void run(String... args) {
        logger.info("EXECUTING : command line runner");

        TransformResults results = new TransformResults();
        TransformControl control = cli.parseArgs(args);
        qrzXmlService.setCredentials(control.getQrzUsername(), control.getQrzPassword());


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
            JsonDxccEntities jsonDxccEntities = new DxccJsonReader().read();
            DxccEntities dxccEntities = new DxccEntities();
            try {
                dxccEntities.setup(jsonDxccEntities);
            } catch (ParseException p) {
                logger.severe(p.getMessage());
            }
            control.setDxccEntities(dxccEntities);
            if (control.hasQrzCredentials()) {
                if (!qrzXmlService.refreshSessionKey()) {
                    logger.warning("Could not connect to QRZ.COM, disabling lookups and continuing...");
                }
            }

            logger.info(String.format("Reading input file %s with encoding %s", inPath, control.getEncoding()));
            Adif3 log = reader.read(inPath, control.getEncoding(), false);
            qsos = transformer.transform(log, control, results);
            logger.info(String.format("Writing output file %s with encoding %s", out, control.getEncoding()));
            if (control.getGenerateKml()) {
                kmlWriter.write(control, kml, inBasename, summits, qsos, results);
                if (StringUtils.isNotEmpty(results.getError())) {
                    logger.severe(results.getError());
                }
            }
            // Contest Calculations
            log.getHeader().setPreamble(new ContestResultsCalculator(summits).calculateResults(log));

            writer.write(out, control.getEncoding(), log);
            if (control.isFormattedOutput()) {
                BufferedWriter markdownWriter = null;
                try {
                    File formattedQsoFile = new File(markdown);
                    if (formattedQsoFile.exists()) {
                        if (!formattedQsoFile.delete()) {
                            logger.severe(String.format("Error deleting Markdown file %s, check permissions?", markdown));
                        }
                    }
                    if (formattedQsoFile.createNewFile()) {
                        formatter.getPrintJobConfig().configure(MARKDOWN_CONTROL_FILE,
                                FileProcessorApplication.class.getClassLoader().getResourceAsStream(MARKDOWN_CONTROL_FILE));
                        logger.info(String.format("Writing Markdown to: %s", markdown));
                        StringBuilder sb = formatter.format(qsos);
                        markdownWriter = Files.newBufferedWriter(formattedQsoFile.toPath(), Charset.forName(formatter.getPrintJobConfig().getOutEncoding()), StandardOpenOption.WRITE);
                        markdownWriter.write(sb.toString());
                    } else {
                        logger.severe(String.format("Error creating Markdown file %s, check permissions?", markdown));
                    }
                } catch (IOException ioe) {
                    logger.severe(String.format("Exception caught whilst writing Markdown file %s: %s", markdown, ioe.getMessage()));
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
