package uk.m0nom.adif3;

import org.marsik.ham.adif.AdiReader;
import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.qsofile.QsoFileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.*;

/**
 * Wrapper to the Adif3 library for reading ADIF format files
 */
public class Adif3FileReader implements QsoFileReader {

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
}
