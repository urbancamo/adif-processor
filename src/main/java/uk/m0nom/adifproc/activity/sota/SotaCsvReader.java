package uk.m0nom.adifproc.activity.sota;

import org.apache.commons.csv.CSVRecord;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.activity.CsvActivityReader;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Expects a SOTA Summits Database Export file, reformatted as UTF-8 CSV with the following columns retained:
 *
 * SummitCode
 * AltM
 * Longitude
 * Latitude
 * Points
 * BonusPoints
 */
public class SotaCsvReader extends CsvActivityReader {
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/uuuu");

    public SotaCsvReader(String sourceFile) {
        super(ActivityType.SOTA, sourceFile);
    }

    @Override
    protected Activity readRecord(CSVRecord record) throws IllegalArgumentException {
        SotaInfo info = new SotaInfo();

        info.setRef(record.get("SummitCode"));
        info.setName(record.get("SummitName"));
        info.setAltitude(Double.parseDouble(record.get("AltM")));

        info.setCoords(readCoords(record,"Latitude", "Longitude"));
        info.setPoints(Integer.parseInt(record.get("Points")));
        info.setBonusPoints(Integer.parseInt(record.get("BonusPoints")));

        String validFrom = record.get("ValidFrom");
        info.setValidFrom(LocalDate.parse(validFrom, formatter));
        String validTo = record.get("ValidTo");
        info.setValidTo(LocalDate.parse(validTo, formatter));

        return info;
    }
}
