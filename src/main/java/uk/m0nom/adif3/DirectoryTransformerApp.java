package uk.m0nom.adif3;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.marsik.ham.adif.Adif3;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.qrz.QrzXmlService;
import uk.m0nom.activity.ActivityDatabases;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Logger;

public class DirectoryTransformerApp implements Runnable
{
    private static final Logger logger = Logger.getLogger(DirectoryTransformerApp.class.getName());

    private final Adif3Transformer transformer;
    private final Adif3FileReader reader;
    private final Adif3FileWriter writer;
    private final ActivityDatabases summits;
    private final QrzXmlService qrzXmlService;
    private final TransformControl control;

    private final String[] args;

    private final static String configFilePath = "./src/main/resources/adif-processor.yaml";

    public DirectoryTransformerApp(String[] args) {
        this.args = args;
        transformer = new Adif3Transformer();
        reader = new Adif3FileReader();
        writer = new Adif3FileWriter();
        summits = new ActivityDatabases();
        qrzXmlService = new QrzXmlService(null, null);
        control = new TransformControl();
    }

    public static void main( String[] args )
    {
        DirectoryTransformerApp instance = new DirectoryTransformerApp(args);
        instance.run();
    }

    @Override
    public void run() {
        if (args.length != 1) {
            logger.config("Usage: uk.m0nom.adif3.DirectoryTransformerApp <directory>");
            logger.config("       post processes all ADIF files in given directory");
            logger.config("       results stored with a file of the same name with '-fta' added to the filename");
        } else {
            String dir = args[0];
                try {
                    summits.loadData();
                    qrzXmlService.disable();
                    if (!qrzXmlService.getSessionKey()) {
                        logger.warning("Could not connect to QRZ.COM, continuing...");
                    }
                    transformer.configure(new FileInputStream(configFilePath), summits, qrzXmlService);

                    Collection<File> files = FileUtils.listFiles(new File(dir), new String[]{"adi"}, false);
                    for (File in: files) {
                        try {
                            Adif3 log = reader.read(in.getAbsolutePath(), "windows-1252", false);
                            transformer.transform(log, control);

                            // Create output file name from input file name
                            String out = String.format("%s-%s.%s", FilenameUtils.removeExtension(in.toString()),
                                    "fta", "adi");
                            writer.write(out, "windows-1252", log);
                             logger.info(String.format("Wrote: %s", out));
                        } catch (UnsupportedHeaderException ushe) {
                            logger.warning(String.format("Unknown header, skipping file: %s", in.getAbsolutePath()));
                        } catch (Exception e) {
                            logger.severe(String.format("Caught exception %s, processing file, skipping file: %s", e.getMessage(), in.getAbsolutePath()));
                            logger.severe(e.toString());
                        }
                    }
                } catch (IOException ioe) {
                    logger.severe(String.format("FATAL ERROR '%s' reading configuration file: %s", ioe.getMessage(), configFilePath));
                }
            }
        }
    }

