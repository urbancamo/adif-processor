package uk.m0nom.activity.rota;

import org.apache.commons.csv.CSVRecord;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.CsvActivityReader;

/**
 * Railways on the Air CSV reader. I created the CSV file from the https://rota.barac.org.uk/stations stations
 * list for 2021. It had a couple of additional entries following the 2021 contest based on the stations we contacted
 * as GB4LHR.
 */
public class RotaCsvReader extends CsvActivityReader {

    public RotaCsvReader(String sourceFile) {
        super(ActivityType.ROTA, sourceFile);
    }

    @Override
    protected Activity readRecord(CSVRecord record) throws IllegalArgumentException {
        RotaInfo info = new RotaInfo();
        info.setRef(record.get("Callsign"));
        info.setName(record.get("Railway"));
        info.setClub(record.get("Club"));
        info.setWab(record.get("WAB"));
        info.setGrid(record.get("Grid"));
        info.setCoords(readCoords(record, "Latitude", "Longitude"));
        return info;
    }
}
