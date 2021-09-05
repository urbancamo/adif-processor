package uk.m0nom.activity.cota;

import kotlin.text.Regex;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.input.BOMInputStream;
import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityReader;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.wwff.WwffInfo;
import uk.m0nom.coords.LatLongParsers;
import uk.m0nom.maidenheadlocator.MaidenheadLocatorConversion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class CotaCsvReader extends ActivityReader {
    private static final Logger logger = Logger.getLogger(CotaCsvReader.class.getName());
    private final LatLongParsers latLongParsers;

    public CotaCsvReader(String sourceFile) {
        super(ActivityType.WWFF, sourceFile);
        latLongParsers = new LatLongParsers();
    }

    public ActivityDatabase read(InputStream inputStream) throws IOException {
        Map<String, Activity> cotaInfo = new HashMap<>();

        final Reader reader = new InputStreamReader(new BOMInputStream(inputStream), StandardCharsets.UTF_8);
        int line = 1;
        int foundLocationsCount = 0;
        Iterable<CSVRecord> records = CSVFormat.EXCEL.withFirstRecordAsHeader().parse(reader);
        for (CSVRecord record : records) {
            line++;
            //COTA WCA	CASTLES	PREFIX	NAME OF CASTLE	LOCATION	INFORMATION
            CotaInfo info = new CotaInfo();
            try {
                info.setRef(record.get("COTA WCA"));
                info.setNoCastles(record.get("CASTLES"));
                info.setName(record.get("NAME OF CASTLE"));
                info.setLocation(record.get("LOCATION"));
                info.setInformation(record.get("INFORMATION"));
            } catch (IllegalArgumentException e) {
                logger.severe(String.format("Error reading line %d: %s", line, e.getMessage()));
            }
            // The database is all over the place, so we have to do some serious post-processing here.
            if (attemptToExtractLocationInformation(info)) {
                foundLocationsCount++;
            }
            cotaInfo.put(info.getRef(), info);
        }

        logger.info(String.format("Found %d castle locations out of the total %d loaded", foundLocationsCount, line-1));
        return new ActivityDatabase(ActivityType.COTA, cotaInfo);
    }

    private boolean attemptToExtractLocationInformation(CotaInfo cota) {
        // The location information is stored in all sorts of ways, so we have to go through each one
        // Start with the most accurate attempting to parse Latitude/Longitude in all the variants

        // Maidenhead location in location column
        try {
            GlobalCoordinates coords = MaidenheadLocatorConversion.locatorToCoords(cota.getInformation());
            cota.setCoords(coords);
            return true;
        } catch (UnsupportedOperationException e) {
            // Nothing to do here, keep information as is
        }

        // Maidenhead location in information column
        try {
            GlobalCoordinates coords = MaidenheadLocatorConversion.locatorToCoords(cota.getInformation());
            cota.setCoords(coords);
            return true;
        } catch (UnsupportedOperationException e) {
            // Nothing to do here, keep information as is
        }
        return false;
    }
}
