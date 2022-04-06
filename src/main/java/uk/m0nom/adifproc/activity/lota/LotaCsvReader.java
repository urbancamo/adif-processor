package uk.m0nom.adifproc.activity.lota;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVRecord;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.activity.CsvActivityReader;

import java.util.logging.Logger;

/**
 * CSV Reader for the Lighthouses and Lightships on the Air Programme data
 */
@Getter
@Setter
public class LotaCsvReader extends CsvActivityReader {
    private static final Logger logger = Logger.getLogger(LotaCsvReader.class.getName());

    public LotaCsvReader(String sourceFile) {
        super(ActivityType.LOTA, sourceFile);
     }

    @Override
    protected Activity readRecord(CSVRecord record) throws IllegalArgumentException {
        LotaInfo info = new LotaInfo();
        info.setCountry(record.get("Country").trim());
        info.setName(record.get("Lighthouse Name").trim());
        info.setStatus(record.get("Status").trim());

        if (info.getName().contains("Deleted.")) {
            info.setStatus("D");
            info.setName(info.getName().replace("Deleted.", ""));
        }
        info.setDxcc(record.get("DXCC").trim());
        info.setContinent(record.get("Continent").trim());
        info.setLocation(record.get("Location").trim());
        info.setRef(record.get("ILLW").trim());
        info.setCoords(readCoords(record, "Latitude", "Longitude"));
        return info;
    }
}
