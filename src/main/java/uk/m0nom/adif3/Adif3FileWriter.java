package uk.m0nom.adif3;

import org.marsik.ham.adif.AdiWriter;
import org.marsik.ham.adif.Adif3;
import uk.m0nom.qsofile.QsoFileWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.logging.Logger;

/**
 * Wrapper for the Adif3 library for writing ADIF format files
 */
public class Adif3FileWriter implements QsoFileWriter {
    private static final Logger logger = Logger.getLogger(Adif3FileWriter.class.getName());

    public void write(String filename, String encoding, Adif3 log) throws IOException {
        AdiWriter writer = new AdiWriter();
        if (log.getHeader() != null) {
            writer.append(log.getHeader(), true);
        }

        log.getRecords().forEach(writer::append);


        try (FileWriter fileWriter = new FileWriter(filename, Charset.forName(encoding));
             BufferedWriter out = new BufferedWriter(fileWriter))
        {
            out.write(writer.toString());
        }
    }

}
