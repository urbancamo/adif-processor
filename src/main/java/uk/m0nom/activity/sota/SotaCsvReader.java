package uk.m0nom.activity.sota;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
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

    public SotaCsvReader() {
        super(ActivityType.SOTA);
    }

    public SotaSummitsDatabase read(InputStream inputStream) throws IOException {
        Map<String, SotaSummitInfo> summitInfo = new HashMap<>();

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);

        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            SotaSummitInfo info = new SotaSummitInfo();

            info.ref = record.get("SummitCode");
            info.name = record.get("SummitName");
            info.altitude = Double.parseDouble(record.get("AltM"));

            info.coords = readCoords(record, "Longitude", "Latitude");
            info.points = Integer.parseInt(record.get("Points"));
            info.bonusPoints = Integer.parseInt(record.get("BonusPoints"));

            summitInfo.put(info.ref, info);
        }

        return new SotaSummitsDatabase(summitInfo);
    }
}
