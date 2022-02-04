package uk.m0nom.activity.hema;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityReader;
import uk.m0nom.activity.ActivityType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * HuMPS on the Air CSV reader - the export having been provided by Rob.
 */
public class HemaCsvReader extends ActivityReader {

    public HemaCsvReader(String sourceFile) {
        super(ActivityType.HEMA, sourceFile);
    }

    public ActivityDatabase read(InputStream inputStream) throws IOException {
        Map<String, Activity> summitInfo = new HashMap<>();

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);

        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            HemaInfo info = new HemaInfo();
            info.setKey(Integer.parseInt(record.get("hHillKey")));

            info.setRef(record.get("hFullReference"));
            info.setAltitude(Double.parseDouble(record.get("hHeightM")));

            info.setCoords(readCoords(record, "hLatitude", "hLongitude"));

            info.setActive(StringUtils.equals(record.get("hActive"), "Y"));
            info.setName(record.get("hName"));

            summitInfo.put(info.getRef(), info);
        }

        return new ActivityDatabase(ActivityType.HEMA, summitInfo);
    }
}
