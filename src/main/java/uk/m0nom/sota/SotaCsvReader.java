package uk.m0nom.sota;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;

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
public class SotaCsvReader {

    public static SotaSummitsDatabase read(InputStream inputStream) throws IOException {
        Map<String, SotaSummitInfo> summitInfo = new HashMap<>();

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);

        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            SotaSummitInfo info = new SotaSummitInfo();

            info.summitCode = record.get("SummitCode");
            info.name = record.get("SummitName");
            info.altitude = Double.parseDouble(record.get("AltM"));
            info.longitude = Double.parseDouble(record.get("Longitude"));
            info.latitude = Double.parseDouble(record.get("Latitude"));
            info.points = Integer.parseInt(record.get("Points"));
            info.bonusPoints = Integer.parseInt(record.get("BonusPoints"));

            summitInfo.put(info.summitCode, info);
        }

        return new SotaSummitsDatabase(summitInfo);
    }
}
