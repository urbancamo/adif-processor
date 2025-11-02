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

        info.setRef(record.get("Ref"));
        info.setName(record.get("Name"));
        info.setBunkerType(record.get("Type"));
        info.setArea(record.get("Area"));
        info.setCoords(readCoords(record,"Lat", "Long"));
        info.setOsgr(record.get("Grid"));
        info.setWab(record.get("WAB"));
        info.setNearestPostcode(record.get("Near Pcode"));
        info.setGrid(record.get("Locator"));
        info.setW3w(record.get("W3W"));
        info.setNote(record.get("Note"));

        return info;
    }
}
