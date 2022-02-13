package uk.m0nom.activity.gma;

import org.apache.commons.csv.CSVRecord;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.CsvActivityReader;

/**
 * Expects a Global Mountain Activity Database Export file
 */
public class GmaCsvReader extends CsvActivityReader {

    public GmaCsvReader(String sourceFile) {
        super(ActivityType.GMA, sourceFile);
    }

    @Override
    protected Activity readRecord(CSVRecord record) throws IllegalArgumentException {
        GmaInfo info = new GmaInfo();

        info.setRef(record.get("Reference"));
        info.setName(record.get("Name"));
        info.setAltitude(Double.parseDouble(record.get("Height (m)")));

        info.setCoords(readCoords(record,"Latitude", "Longitude"));
        info.setGrid(record.get("Maidenhead Locator"));

        return info;
    }
}
