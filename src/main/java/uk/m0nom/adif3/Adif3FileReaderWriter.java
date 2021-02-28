package uk.m0nom.adif3;

import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.AdiWriter;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Optional;

public class Adif3FileReaderWriter {
    public Adif3 read(String filename, String encoding) throws IOException {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader =
                Files.newBufferedReader(new File(filename).toPath(), Charset.forName(encoding));

        Optional<Adif3> result = reader.read(inputReader);
        if (result.isPresent()) {
            return result.get();
        }
        return null;
    }

    public void write(String filename, Adif3 log) throws IOException {
        AdiWriter writer = new AdiWriter();
        writer.append(log.getHeader(), true);

        for (Adif3Record rec : log.getRecords()) {
            writer.append(rec);
        }

        FileWriter fileWriter = null;
        BufferedWriter out = null;
        try {
            fileWriter = new FileWriter(filename);
            out = new BufferedWriter(fileWriter);
        } finally {
            out.write(writer.toString());
            out.close();
            fileWriter.close();
        }
    }
}
