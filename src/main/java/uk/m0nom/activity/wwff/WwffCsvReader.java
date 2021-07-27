package uk.m0nom.activity.wwff;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityReader;
import uk.m0nom.activity.ActivityType;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class WwffCsvReader extends ActivityReader {
    private static final Logger logger = Logger.getLogger(WwffCsvReader.class.getName());
    private static final String EMPTY_DATE = "0000-00-00";

    public WwffCsvReader(String sourceFile) {
        super(ActivityType.WWFF, sourceFile);
    }

    public ActivityDatabase read(InputStream inputStream) throws IOException {
        Map<String, Activity> wwffInfo = new HashMap<>();

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);

        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            WwffInfo info = new WwffInfo();

            info.setRef(record.get("reference"));
            info.setName(record.get("name"));
            info.setActive(StringUtils.equals(record.get("status"), "active"));
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
                logger.severe(String.format("validFrom date for WWFF ref: %s is invalid, string form is: %s", info.getRef(), validFrom));
            }
            String validTo = record.get("validTo");
            try {
                if (isValidDate(validTo)) {
                    info.setValidTo((df.parse(validTo)));
                }
            } catch (ParseException pe) {
                logger.severe(String.format("validTo date for WWFF ref: %s is invalid, string form is: %s", info.getRef(), validTo));
            }

            info.setNotes(record.get("notes"));
            info.setLastMod(record.get("lastMod"));
            info.setChangeLog(record.get("changeLog"));
            info.setReviewFlag(record.get("reviewFlag"));

            info.setSpecialFlags(record.get("specialFlags"));
            info.setWebsite(record.get("website"));
            info.setCountry(record.get("country"));
            info.setRegion(record.get("region"));

            wwffInfo.put(info.getRef(), info);
        }

        return new ActivityDatabase(ActivityType.WWFF, wwffInfo);
    }

    private boolean isValidDate(String dateString) {
        return StringUtils.isNotEmpty(dateString) &&
                dateString.length() == EMPTY_DATE.length() &&
                !StringUtils.equals(dateString, EMPTY_DATE);
    }
}
