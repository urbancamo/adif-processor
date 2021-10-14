package uk.m0nom.activity.lota;

import uk.m0nom.activity.Activity;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;
import uk.m0nom.coords.LocationParsers;
import uk.m0nom.coords.LocationSource;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.logging.Logger;

public class LotaLocationProcessor {
    private static final Logger logger = Logger.getLogger(LotaLocationProcessor.class.getName());

    private static LocationParsers locationParsers = null;

     public static void main(String[] args) throws FileNotFoundException {
        ActivityDatabases summits = new ActivityDatabases();

        summits.loadData();
        locationParsers = new LocationParsers(summits);
        int count = extractLocationInformation(summits.getDatabase(ActivityType.LOTA));
        logger.info(String.format("Found %d %s locations", count, ActivityType.LOTA.getActivityDescription()));

         new LotaCsvWriter().write(summits.getDatabase(ActivityType.LOTA));
    }

    private static int extractLocationInformation(ActivityDatabase lotaDb) {
        Collection<Activity> values = lotaDb.getValues();
        int count = 0;
        for (Activity activity : values) {
            LotaInfo Lota = (LotaInfo) activity;
            if (extractLocationInformation(Lota)) {
                count++;
            }
        }
        return count;
    }

    private static boolean extractLocationInformation(LotaInfo lota) {
        // The location information is stored in all sorts of ways, so we have to go through each one
        // Start with the most accurate attempting to parse Latitude/Longitude in all the variants
        GlobalCoordinatesWithSourceAccuracy coords = locationParsers.parseStringForCoordinates(LocationSource.ACTIVITY, lota.getLocation());
        if (coords != null) {
            lota.setCoords(coords);
            //lota.setAltitude(coords.getAltitude());
            return true;
        }
        return false;
    }

}
