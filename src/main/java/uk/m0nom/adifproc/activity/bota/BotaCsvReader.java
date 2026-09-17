package uk.m0nom.adifproc.activity.bota;

import org.apache.commons.csv.CSVRecord;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.activity.CsvActivityReader;

/**
 * Expects a Bunkers on the Air CSV file - generated using the CSV export functionality
 */
public class BotaCsvReader extends CsvActivityReader {

    public BotaCsvReader(String sourceFile) {
        super(ActivityType.BOTA, sourceFile);
    }

    @Override
    protected Activity readRecord(CSVRecord record) throws IllegalArgumentException {
        BotaInfo info = new BotaInfo();

        info.setRef(record.get("Reference"));
        info.setName(record.get("Name"));
        info.setBunkerType(record.get("Type"));
        info.setArea(record.get("Historic County"));
        info.setCoords(readCoords(record,"Lat", "Long"));
        info.setNearestPostcode(record.get("Nearest Postcode"));

        return info;
    }
}
