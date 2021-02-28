package uk.m0nom.adif3;

import org.apache.commons.io.FilenameUtils;
import org.marsik.ham.adif.Adif3;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;


public class PrintFormatApp implements Runnable
{
    private static PrintFormatApp instance;

    private Adif3PrintFormatter formatter;
    private Adif3FileReaderWriter readerWriter;

    private String args[];

    public PrintFormatApp(String args[]) {
        this.args = args;
        formatter = new Adif3PrintFormatter();
        readerWriter = new Adif3FileReaderWriter();
    }

    public static void main( String[] args )
    {
        instance = new PrintFormatApp(args);
        instance.run();
    }

    @Override
    public void run() {
        BufferedWriter writer = null;
        String in = args[0];
        String out = String.format("%s.%s", FilenameUtils.removeExtension(in.toString()), "prn");
        try {
            if (args.length != 2) {
                System.err.println(String.format("Usage: %s <inputFile>.adi <config-file>.yaml", this.getClass().getName()));
            } else {
                formatter.configure(args[1]);
                Adif3 log = readerWriter.read(in, formatter.getPrintJobConfig().getInEncoding());
                StringBuilder sb = formatter.format(log);
                File outFile = new File(out);
                outFile.delete();
                outFile.createNewFile();
                writer = Files.newBufferedWriter(new File(out).toPath(), Charset.forName(formatter.getPrintJobConfig().getOutEncoding()), StandardOpenOption.WRITE);
                writer.write(sb.toString());
            }
        } catch(IOException e){
                System.err.println(String.format("Caught exception processing file: %s exception is:\n\t%s", in, e.getMessage()));
                e.printStackTrace(System.err);
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    System.err.println(String.format("Caught exception closing output file %s: %s", out, e.getMessage()));
                }
            }
        }
    }
}
