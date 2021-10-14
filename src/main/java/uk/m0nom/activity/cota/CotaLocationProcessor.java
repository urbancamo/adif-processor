package uk.m0nom.activity.cota;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;
import uk.m0nom.coords.LocationParsers;
import uk.m0nom.coords.LocationSource;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Locale;
import java.util.logging.Logger;

public class CotaLocationProcessor {
    private static final Logger logger = Logger.getLogger(CotaLocationProcessor.class.getName());

    private static LocationParsers locationParsers = null;

     public static void main(String[] args) throws FileNotFoundException {
        ActivityDatabases summits = new ActivityDatabases();

        summits.loadData();
        locationParsers = new LocationParsers(summits);
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
            GlobalCoordinatesWithSourceAccuracy coords = locationParsers.parseStringForCoordinates(LocationSource.ACTIVITY, cota.getLocation());
            if (coords != null) {
                cota.setCoords(coords);
                return true;
            }
        }
        if (isNotUrl(cota.getInformation())) {
            GlobalCoordinatesWithSourceAccuracy coords = locationParsers.parseStringForCoordinates(LocationSource.ACTIVITY, cota.getInformation());
            if (coords != null) {
                cota.setCoords(coords);
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
