package uk.m0nom.adifproc.activity.wota;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.Strings;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityDatabase;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.activity.CsvActivityReader;

import java.io.IOException;
import java.io.InputStream;

/**
 * Expects a WOTA Summits Database Export file, reformatted as UTF-8 CSV with the following columns retained:
 * <p>
 * wotaid
 * sotaid
 * book
 * name
 * height
 * reference
 * humpid
 * gridid
 */
public class WotaCsvReader extends CsvActivityReader {

    public WotaCsvReader(String sourceFile) {
        super(ActivityType.WOTA, sourceFile);
    }

    @Override
    public ActivityDatabase read(InputStream reader) throws IOException {
        return new WotaSummitsDatabase(ActivityType.WOTA, readRecords(reader));
    }

    @Override
    protected Activity readRecord(CSVRecord record) throws IllegalArgumentException {
        WotaInfo info = new WotaInfo();

        int wotaid = Integer.parseInt(record.get("wotaid"));
        if (wotaid <= 214) {
            info.setRef(String.format("LDW-%03d", wotaid));
        } else {
            info.setRef(String.format("LDO-%03d", wotaid - 214));
        }
        info.setInternalId(wotaid);

        String sotaId = record.get("sotaid");
        if (!Strings.CI.equals(sotaId, "NULL")) {
            info.setSotaId(String.format("G/LD-%03d", Integer.parseInt(sotaId)));
        }

        info.setBook(record.get("book"));
        info.setName(record.get("name"));
        info.setAltitude(Double.valueOf(record.get("height")));
        info.setReference(record.get("reference"));

        String humpId = record.get("humpid");
        if (!Strings.CI.equals(humpId, "NULL")) {
            info.setHemaId(String.format("G/HLD-%03d", Integer.parseInt(humpId)));
        }
        info.setGridId(record.get("gridid"));

        info.setX(Integer.parseInt(record.get("x")));
        info.setY(Integer.parseInt(record.get("y")));

        info.setCoords(readCoords(record, "lat", "long"));
        return info;
    }
}
