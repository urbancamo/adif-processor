package uk.m0nom.adifproc.adif3.cli;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.marsik.ham.adif.Adif3;
import uk.m0nom.adifproc.adif3.io.Adif3FileReader;
import uk.m0nom.adifproc.adif3.print.Adif3PrintFormatter;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.logging.Logger;

/**
 * Command line interface to the print formatter mainly for checking out/testing new features
 * TODO Needs refactoring now the formatter is processing Qsos and not Adif3 Records
 */
public class PrintFormatApp implements Runnable
{
    private static final Logger logger = Logger.getLogger(PrintFormatApp.class.getName());

    private final Adif3PrintFormatter formatter;

    private final String[] args;

    public PrintFormatApp(String[] args) {
        this.args = args;
        formatter = new Adif3PrintFormatter();
        Adif3FileReader readerWriter = new Adif3FileReader();
    }

    public static void main( String[] args )
    {
        PrintFormatApp instance = new PrintFormatApp(args);
        instance.run();
    }

    @Override
    public void run() {
        BufferedWriter writer = null;
        String in = "<unspecified>";
        String out = "<unspecified>";
        try {
            if (args.length != 2) {
                logger.config(String.format("Usage: %s <inputFile>.adi <config-file>.yaml", this.getClass().getName()));
            } else {
                in = args[0];
                out = String.format("%s.%s", FilenameUtils.removeExtension(in), "prn");
                formatter.getPrintJobConfig().configure(args[1], new FileInputStream(args[1]));
                //Adif3 log = readerWriter.read(in, formatter.getPrintJobConfig().getInEncoding(), true);
                //StringBuilder sb = formatter.format(log);
                File outFile = new File(out);
                if (outFile.delete()) {
                    if (outFile.createNewFile()) {
                        writer = Files.newBufferedWriter(new File(out).toPath(), Charset.forName(formatter.getPrintJobConfig().getOutEncoding()), StandardOpenOption.WRITE);
                        //writer.write(sb.toString());
                    } else {
                        logger.severe(String.format("Error creating new file: %s", outFile.getAbsolutePath()));
                    }
                } else {
                    logger.severe(String.format("Error deleting file: %s", outFile.getAbsolutePath()));
                }
            }
        } catch(IOException e){
                logger.severe(String.format("Caught exception processing file: %s exception is:\n\t%s", in, e.getMessage()));
                logger.severe(ExceptionUtils.getStackTrace(e));
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    logger.severe(String.format("Caught exception closing output file %s: %s", out, e.getMessage()));
                    logger.severe(ExceptionUtils.getStackTrace(e));
                }
            }
        }
    }
}
