package uk.m0nom.adif3;

import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.AdiWriter;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

public class Adif3FileReaderWriter {
    /**
     * Compare two QSOs based on their date, if date is the same then the time on.
     * This allows the QSO list to be sorted chronologically.
     */
    class Adif3RecordTimestampComparator implements Comparator<Adif3Record> {
        @Override
        public int compare(Adif3Record o1, Adif3Record o2) {
            int dateCompare = o1.getQsoDate().compareTo(o2.getQsoDate());
            int timeCompare = o1.getTimeOn().compareTo(o2.getTimeOn());
            if (dateCompare != 0) {
                return dateCompare;
            }
            return timeCompare;
        }
    }
    public Adif3 read(String filename, String encoding) throws IOException {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader =
                Files.newBufferedReader(new File(filename).toPath(), Charset.forName(encoding));

        Optional<Adif3> result = reader.read(inputReader);
        if (result.isPresent()) {
            Adif3 adif = result.get();
            SortedSet<Adif3Record> sortedRecords = new TreeSet<>(new Adif3RecordTimestampComparator());
            for (Adif3Record record : adif.getRecords()) {
                sortedRecords.add(record);
            }
            List<Adif3Record> sortedRecordList = new ArrayList<>();
            sortedRecordList.addAll(sortedRecords);
            adif.setRecords(sortedRecordList);
            return adif;
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
