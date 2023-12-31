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

        info.setRef(record.get("UKBOTA Reference"));
        info.setName(record.get("Bunker Name"));
        info.setBunkerType(record.get("Type"));
        info.setArea(record.get("Area"));
        info.setCoords(readCoords(record,"Latitude", "Longitude"));
        info.setOsgr(record.get("OSGR"));
        info.setWab(record.get("WAB"));
        info.setNearestPostcode(record.get("Nearest Postcode"));
        info.setGrid(record.get("Maidenhead"));

        return info;
    }
}
