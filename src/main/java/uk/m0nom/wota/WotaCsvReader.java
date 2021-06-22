package uk.m0nom.wota;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.sota.SotaSummitInfo;
import uk.m0nom.sota.SotaSummitsDatabase;

import java.io.*;
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
public class WotaCsvReader {
    public static WotaSummitsDatabase read(InputStream inputStream) throws IOException {
        Map<String, WotaSummitInfo> summitInfo = new HashMap<>();

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), "UTF-8");

        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            WotaSummitInfo info = new WotaSummitInfo();

            int wotaid = Integer.parseInt(record.get("wotaid"));
            if (wotaid <= 214) {
                info.wotaId = String.format("LDW-%03d", wotaid);
            } else {
                info.wotaId = String.format("LDO-%03d", wotaid - 214);
            }
            String sotaId = record.get("sotaid");
            if (!StringUtils.equals(sotaId, "NULL")) {
                info.sotaId = String.format("G/LD-%03d", Integer.parseInt(sotaId));
            }

            info.book = record.get("book");
            info.name = record.get("name");
            info.height = Integer.parseInt(record.get("height"));
            info.reference = record.get("reference");

            String humpId = record.get("humpid");
            if (!StringUtils.equals(humpId, "NULL")) {
                info.hemaId = String.format("G/HLD-%03d", Integer.parseInt(humpId));
            }
            info.gridId = record.get("gridid");

            info.x = Integer.parseInt(record.get("x"));
            info.y = Integer.parseInt(record.get("y"));
            info.latitude = Double.parseDouble(record.get("lat"));
            info.longitude = Double.parseDouble(record.get("long"));

            summitInfo.put(info.wotaId, info);
        }

        return new WotaSummitsDatabase(summitInfo);
    }
}
