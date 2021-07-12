package uk.m0nom.adif3;

import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.AdiWriter;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;
import java.util.logging.Logger;

public class Adif3FileReaderWriter {
    private static final Logger logger = Logger.getLogger(Adif3FileReaderWriter.class.getName());

    public Adif3 read(String filename, String encoding, boolean sort) throws IOException {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader =
                Files.newBufferedReader(new File(filename).toPath(), Charset.forName(encoding));

        Optional<Adif3> result = reader.read(inputReader);
        if (result.isPresent()) {
            Adif3 adif = result.get();
            if (sort) {
                int unsortedRecords = adif.getRecords().size();
                SortedSet<Adif3Record> sortedRecords = new TreeSet<>(new Adif3RecordTimestampComparator());
                sortedRecords.addAll(adif.getRecords());
                List<Adif3Record> sortedRecordList = new ArrayList<>(sortedRecords);
                assert (sortedRecordList.size() == unsortedRecords);
                adif.setRecords(sortedRecordList);
            }
            return adif;
        }
        return null;
    }

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
