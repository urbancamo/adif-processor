package uk.m0nom.adifproc.activity;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public abstract class CsvActivityReader extends ActivityReader {
    private static final Logger logger = Logger.getLogger(CsvActivityReader.class.getName());

    public CsvActivityReader(ActivityType type,  String sourceFile) {
        super(type, sourceFile);
    }

    private int line = 0;

    public Map<String, Activity> readRecords(InputStream inputStream) throws IOException {
        Map<String, Activity> activityMap = new HashMap<>();
        line = 0;
        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);

        try {
            records.forEach(record -> {
                    Activity info = readRecord(record);
                    activityMap.put(info.getRef(), info);
                    line++;
            });
        } catch (IllegalArgumentException e) {
            logger.severe(String.format("Error reading line %d: %s", line, e.getMessage()));
        }

        return activityMap;
    }

    public ActivityDatabase read(InputStream reader) throws IOException {
        return new ActivityDatabase(getType(), readRecords(reader));
    }

    protected abstract Activity readRecord(CSVRecord record) throws IllegalArgumentException;

}
