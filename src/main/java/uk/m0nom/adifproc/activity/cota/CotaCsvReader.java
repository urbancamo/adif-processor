package uk.m0nom.adifproc.activity.cota;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.csv.CSVRecord;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.activity.CsvActivityReader;

import java.util.logging.Logger;

/**
 * Reader for the Castles on the Air CSV file, which was created from an export of the 'master' spreadsheet
 */
@Getter
@Setter
public class CotaCsvReader extends CsvActivityReader {
    private static final Logger logger = Logger.getLogger(CotaCsvReader.class.getName());

    public CotaCsvReader(String sourceFile) {
        super(ActivityType.COTA, sourceFile);
     }

     @Override
     protected Activity readRecord(CSVRecord record) throws IllegalArgumentException {
         CotaInfo info = new CotaInfo();
         info.setRef(record.get("COTA WCA").trim());
         info.setNoCastles(record.get("CASTLES").trim());
         info.setPrefix(record.get("PREFIX").trim());
         info.setName(record.get("NAME OF CASTLE").trim());
         info.setLocation(record.get("LOCATION").trim());
         info.setInformation(record.get("INFORMATION").trim());
         info.setCoords(readCoords(record, "LAT", "LONG"));
         return info;
    }
}
