package uk.m0nom.adif3;

import org.marsik.ham.adif.AdiWriter;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
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

        for (Adif3Record rec : log.getRecords()) {
            writer.append(rec);
        }

        FileWriter fileWriter = null;
        BufferedWriter out = null;
        try {
            fileWriter = new FileWriter(filename, Charset.forName(encoding));
            out = new BufferedWriter(fileWriter);
        } finally {
            assert out != null;
            out.write(writer.toString());
            out.close();
            try {
                fileWriter.close();
            } catch (Exception e) {
                logger.severe(String.format("Eror closing output file: %s, error is: %s", filename, e.getMessage()));
            }
        }
    }

}
