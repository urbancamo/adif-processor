package uk.m0nom.activity.hema;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.ActivityReader;
import uk.m0nom.activity.ActivityType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HemaCsvReader extends ActivityReader {

    public HemaCsvReader() {
        super(ActivityType.HEMA);
    }

    public HemaSummitsDatabase read(InputStream inputStream) throws IOException {
        Map<String, HemaSummitInfo> summitInfo = new HashMap<>();

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);

        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            HemaSummitInfo info = new HemaSummitInfo();
            info.key = Integer.parseInt(record.get("hHillKey"));

            info.ref = record.get("hFullReference");
            info.altitude = Double.parseDouble(record.get("hHeightM"));

            info.coords = readCoords(record, "hLongitude", "hLatitude");

            info.active = StringUtils.equals(record.get("hActive"), "Y");
            info.name = record.get("hName");

            summitInfo.put(info.ref, info);
        }

        return new HemaSummitsDatabase(summitInfo);
    }
}
