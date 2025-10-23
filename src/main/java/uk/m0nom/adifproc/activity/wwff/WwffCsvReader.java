package uk.m0nom.adifproc.activity.wwff;

import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;
import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.activity.CsvActivityReader;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.IllformedLocaleException;

/**
 * Reader for the Worldwide Flora Fauna CSV extract file
 */
public class WwffCsvReader extends CsvActivityReader {
    private static final String EMPTY_DATE = "0000-00-00";
    private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

    public WwffCsvReader(String sourceFile) {
        super(ActivityType.WWFF, sourceFile);
    }


    @Override
    protected Activity readRecord(CSVRecord record) throws IllegalArgumentException {
        WwffInfo info = new WwffInfo();

        info.setRef(record.get("reference"));
        info.setName(record.get("name"));
        info.setActive(Strings.CI.equals(record.get("status"), "active"));
        info.setCoords(readCoords(record, "latitude", "longitude"));

        info.setProgram(record.get("program"));
        info.setDxcc(record.get("dxcc"));
        info.setState(record.get("state"));
        info.setCounty(record.get("county"));
        info.setContinent(record.get("continent"));
        info.setIota(record.get("iota"));
        info.setIaruLocator(record.get("iaruLocator"));
        info.setIUCNcat(record.get("IUCNcat"));

        String validFrom = record.get("validFrom");
        try {
            if (isValidDate(validFrom)) {
                info.setValidFrom((df.parse(validFrom)));
            }
        } catch (ParseException pe) {
            throw new IllformedLocaleException(String.format("validFrom date for WWFF ref: %s is invalid, string form is: %s", info.getRef(), validFrom));
        }
        String validTo = record.get("validTo");
        try {
            if (isValidDate(validTo)) {
                info.setValidTo((df.parse(validTo)));
            }
        } catch (ParseException pe) {
            throw new IllegalArgumentException(String.format("validTo date for WWFF ref: %s is invalid, string form is: %s", info.getRef(), validTo));
        }

        info.setNotes(record.get("notes"));
        info.setLastMod(record.get("lastMod"));
        info.setChangeLog(record.get("changeLog"));
        info.setReviewFlag(record.get("reviewFlag"));

        info.setSpecialFlags(record.get("specialFlags"));
        info.setWebsite(record.get("website"));
        info.setCountry(record.get("country"));
        info.setRegion(record.get("region"));
        return info;
    }

    private boolean isValidDate(String dateString) {
        return StringUtils.isNotEmpty(dateString) &&
                dateString.length() == EMPTY_DATE.length() &&
                !Strings.CI.equals(dateString, EMPTY_DATE);
    }
}
