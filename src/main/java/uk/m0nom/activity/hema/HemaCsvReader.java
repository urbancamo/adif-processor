package uk.m0nom.activity.hema;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.CsvActivityReader;

/**
 * HuMPS on the Air CSV reader - the export having been provided by Rob.
 */
public class HemaCsvReader extends CsvActivityReader {

    public HemaCsvReader(String sourceFile) {
        super(ActivityType.HEMA, sourceFile);
    }

    @Override
    protected Activity readRecord(CSVRecord record) throws IllegalArgumentException {
        HemaInfo info = new HemaInfo();
        info.setKey(Integer.parseInt(record.get("hHillKey")));

        info.setRef(record.get("hFullReference"));
        info.setAltitude(Double.parseDouble(record.get("hHeightM")));

        info.setCoords(readCoords(record, "hLatitude", "hLongitude"));

        info.setActive(StringUtils.equals(record.get("hActive"), "Y"));
        info.setName(record.get("hName"));

        return info;
    }
}
