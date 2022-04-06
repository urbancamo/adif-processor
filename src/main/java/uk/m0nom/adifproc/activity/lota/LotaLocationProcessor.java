package uk.m0nom.adifproc.activity.lota;

import uk.m0nom.adifproc.activity.Activity;
import uk.m0nom.adifproc.activity.ActivityDatabase;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.coords.LocationParserResult;
import uk.m0nom.adifproc.coords.LocationParsingService;
import uk.m0nom.adifproc.coords.LocationSource;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.logging.Logger;

/**
 * Used to standardize the various location formats used in the master Lighthouses extract file
 */
public class LotaLocationProcessor {
    private static final Logger logger = Logger.getLogger(LotaLocationProcessor.class.getName());

    private static LocationParsingService locationParsingService = null;

     public static void main(String[] args) throws FileNotFoundException {
        ActivityDatabaseService summits = new ActivityDatabaseService();

        summits.loadData();
        locationParsingService = new LocationParsingService();
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
        LocationParserResult result = locationParsingService.parseStringForCoordinates(LocationSource.ACTIVITY, lota.getLocation());
        if (result != null) {
            lota.setCoords(result.getCoords());
            //lota.setAltitude(coords.getAltitude());
            return true;
        }
        return false;
    }

}
