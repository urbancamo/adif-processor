package uk.m0nom.adifproc.activity.cota;

import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityDatabase;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.coords.LocationParserResult;
import uk.m0nom.adifproc.coords.LocationParsingService;
import uk.m0nom.adifproc.coords.LocationSource;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * The original Castles on the Air data was a spreadsheet split per country. Each country used a different
 * standard for defining a castle location, and sometimes they even varied per sheet. This code was the
 * original user of the coords package to handle the multitude of location formats used. In the end the
 * coords code was reused in the Coordinate Converter.
 */
public class CotaLocationProcessor {
    private static final Logger logger = Logger.getLogger(CotaLocationProcessor.class.getName());

    private static LocationParsingService locationParsingService = null;

     public static void main(String[] args) throws FileNotFoundException {
        ActivityDatabaseService summits = new ActivityDatabaseService();

        summits.loadData();
        locationParsingService = new LocationParsingService();
        int count = extractLocationInformation(summits.getDatabase(ActivityType.COTA));
        logger.info(String.format("Found %d castle locations", count));

         new CotaCsvWriter().write(summits.getDatabase(ActivityType.COTA));
    }

    private static int extractLocationInformation(ActivityDatabase cotaDb) {
        Collection<Activity> values = cotaDb.getValues();
        int count = 0;
        for (Activity activity : values) {
            CotaInfo cota = (CotaInfo) activity;
            if (extractLocationInformation(cota)) {
                count++;
            }
        }
        return count;
    }

    private static boolean extractLocationInformation(CotaInfo cota) {
        // The location information is stored in all sorts of ways, so we have to go through each one
        // Start with the most accurate attempting to parse Latitude/Longitude in all the variants
        if (isNotUrl(cota.getLocation())) {
            LocationParserResult result = locationParsingService.parseStringForCoordinates(LocationSource.ACTIVITY, cota.getLocation());
            if (result != null) {
                cota.setCoords(result.getCoords());
                return true;
            }
        }
        if (isNotUrl(cota.getInformation())) {
            LocationParserResult result = locationParsingService.parseStringForCoordinates(LocationSource.ACTIVITY, cota.getInformation());
            if (result != null) {
                cota.setCoords(result.getCoords());
                return true;
            }
        }
        return false;
    }

    private static boolean isNotUrl(String str) {
         String toCheck = str.trim().toLowerCase(Locale.ROOT);
         return (!toCheck.startsWith("http://") && !toCheck.startsWith("https://"));
    }
}
