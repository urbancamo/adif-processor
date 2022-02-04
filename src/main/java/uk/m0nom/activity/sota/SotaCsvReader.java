package uk.m0nom.activity.sota;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityReader;
import uk.m0nom.activity.ActivityType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Expects a SOTA Summits Database Export file, reformatted as UTF-8 CSV with the following columns retained:
 *
 * SummitCode
 * AltM
 * Longitude
 * Latitude
 * Points
 * BonusPoints
 */
public class SotaCsvReader extends ActivityReader {

    public SotaCsvReader(String sourceFile) {
        super(ActivityType.SOTA, sourceFile);
    }

    public ActivityDatabase read(InputStream inputStream) throws IOException {
        Map<String, Activity> summitInfo = new HashMap<>();

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);

        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            SotaInfo info = new SotaInfo();

            info.setRef(record.get("SummitCode"));
            info.setName(record.get("SummitName"));
            info.setAltitude(Double.parseDouble(record.get("AltM")));

            info.setCoords(readCoords(record,"Latitude", "Longitude"));
            info.setPoints(Integer.parseInt(record.get("Points")));
            info.setBonusPoints(Integer.parseInt(record.get("BonusPoints")));

            summitInfo.put(info.getRef(), info);
        }

        return new ActivityDatabase(ActivityType.SOTA, summitInfo);
    }
}
