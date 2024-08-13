package uk.m0nom.adifproc.adif3.io;

import org.marsik.ham.adif.AdiWriter;
import org.marsik.ham.adif.Adif3;
import org.springframework.stereotype.Component;
import uk.m0nom.adifproc.qsofile.QsoFileWriter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Wrapper for the Adif3 library for writing ADIF format files
 */
@Component
public class Adif3FileWriter implements QsoFileWriter {

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
