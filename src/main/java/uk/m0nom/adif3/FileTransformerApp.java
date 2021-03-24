package uk.m0nom.adif3;

import org.apache.commons.io.FilenameUtils;
import org.marsik.ham.adif.Adif3;

import java.io.IOException;

public class FileTransformerApp implements Runnable
{

    private static FileTransformerApp instance;

    private Adif3Transformer transformer;
    private Adif3FileReaderWriter readerWriter;

    private final static String configFilePath = "adif-processor.yaml";

    private String args[];

    public FileTransformerApp(String args[]) {
        this.args = args;
        transformer = new Adif3Transformer();
        readerWriter = new Adif3FileReaderWriter();
    }

    public static void main( String[] args )
    {
        instance = new FileTransformerApp(args);
        instance.run();
    }

    @Override
    public void run() {
        if (args.length < 1 || args.length > 2) {
            System.err.println(String.format("Usage: %s <inputFile>.adi [<outputFile>.adi]", this.getClass().getName()));
        } else {
            String in = args[0];
            String out = String.format("%s-%s.%s", FilenameUtils.removeExtension(in.toString()), "fta", "adi");
            if (args.length == 2) {
                out = args[1];
            }

            try {
                transformer.configure(configFilePath);
                Adif3 log = readerWriter.read(in, "windows-1252");
                transformer.transform(log);
                readerWriter.write(out, log);
            } catch (UnsupportedHeaderException ushe) {
                System.err.println(String.format("Unknown header for file: %s", in));
            } catch (IOException e) {
                System.err.println(String.format("Caught exception %s processing file: %s", e.getMessage(), in));
                System.err.println(e.toString());
            }
        }
    }
}
