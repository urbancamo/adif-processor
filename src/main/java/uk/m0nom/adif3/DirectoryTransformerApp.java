package uk.m0nom.adif3;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.marsik.ham.adif.Adif3;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

public class DirectoryTransformerApp implements Runnable
{
    private static DirectoryTransformerApp instance;

    private Adif3Transformer transformer;
    private Adif3FileReaderWriter readerWriter;

    private String args[];

    private final static String configFilePath = "./src/main/resources/adif-processor.yaml";

    public DirectoryTransformerApp(String args[]) {
        this.args = args;
        transformer = new Adif3Transformer();
        readerWriter = new Adif3FileReaderWriter();
    }

    public static void main( String[] args )
    {
        instance = new DirectoryTransformerApp(args);
        instance.run();
    }

    @Override
    public void run() {
        if (args.length != 1) {
            System.err.println("Usage: uk.m0nom.adif3.DirectoryTransformerApp <directory>");
            System.err.println("       post processes all ADIF files in given directory");
            System.err.println("       results stored with a file of the same name with '-fta' added to the filename");
        } else {
            String dir = args[0];
                try {
                    transformer.configure(configFilePath);
                    Collection<File> files = FileUtils.listFiles(new File(dir), new String[]{"adi"}, false);
                    for (File in: files) {
                        try {
                            Adif3 log = readerWriter.read(in.getAbsolutePath(), "windows-1252", false);
                            transformer.transform(log);

                            // Create output file name from input file name
                            String out = String.format("%s-%s.%s", FilenameUtils.removeExtension(in.toString()),
                                    "fta", "adi");
                            readerWriter.write(out, log);
                            System.out.println(String.format("Wrote: %s", out));
                        } catch (UnsupportedHeaderException ushe) {
                            System.err.println(String.format("Unknown header, skipping file: %s", in.getAbsolutePath()));
                        } catch (Exception e) {
                            System.err.println(String.format("Caught exception %s, processing file, skipping file: %s", e.getMessage(), in.getAbsolutePath()));
                            System.err.println(e.toString());
                        }
                    }
                } catch (IOException ioe) {
                    System.err.println(String.format("FATAL ERROR '%s' reading configuration file: %s", ioe.getMessage(), configFilePath));
                }
            }
        }
    }

