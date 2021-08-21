package uk.m0nom.activity.wota;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityReader;
import uk.m0nom.activity.ActivityType;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Expects a WOTA Summits Database Export file, reformatted as UTF-8 CSV with the following columns retained:
 *
 * wotaid
 * sotaid
 * book
 * name
 * height
 * reference
 * humpid
 * gridid
 */
public class WotaCsvReader extends ActivityReader {

    public WotaCsvReader(String sourceFile) {
        super(ActivityType.WOTA, sourceFile);
    }

    public ActivityDatabase read(InputStream inputStream) throws IOException {
        Map<String, Activity> summitInfo = new HashMap<>();

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);

        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            WotaSummitInfo info = new WotaSummitInfo();

            int wotaid = Integer.parseInt(record.get("wotaid"));
            if (wotaid <= 214) {
                info.setRef(String.format("LDW-%03d", wotaid));
            } else {
                info.setRef(String.format("LDO-%03d", wotaid - 214));
            }
            info.setInternalId(wotaid);

            String sotaId = record.get("sotaid");
            if (!StringUtils.equals(sotaId, "NULL")) {
                info.setSotaId(String.format("G/LD-%03d", Integer.parseInt(sotaId)));
            }

            info.setBook(record.get("book"));
            info.setName(record.get("name"));
            info.setAltitude(Double.valueOf(record.get("height")));
            info.setReference(record.get("reference"));

            String humpId = record.get("humpid");
            if (!StringUtils.equals(humpId, "NULL")) {
                info.setHemaId(String.format("G/HLD-%03d", Integer.parseInt(humpId)));
            }
            info.setGridId(record.get("gridid"));

            info.setX(Integer.parseInt(record.get("x")));
            info.setY(Integer.parseInt(record.get("y")));

            info.setCoords(readCoords(record, "lat", "long"));

            summitInfo.put(info.getRef(), info);
        }

        return new WotaSummitsDatabase(ActivityType.WOTA, summitInfo);
    }
}
