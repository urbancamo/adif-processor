package uk.m0nom.adifproc.activity;

import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.activity.bota.BotaCsvReader;
import uk.m0nom.adifproc.activity.cota.CotaCsvReader;
import uk.m0nom.adifproc.activity.gma.GmaCsvReader;
import uk.m0nom.adifproc.activity.hema.HemaCsvReader;
import uk.m0nom.adifproc.activity.iota.IotaJsonReader;
import uk.m0nom.adifproc.activity.lota.LotaCsvReader;
import uk.m0nom.adifproc.activity.pota.PotaCsvReader;
import uk.m0nom.adifproc.activity.rota.RotaCsvReader;
import uk.m0nom.adifproc.activity.sota.SotaCsvReader;
import uk.m0nom.adifproc.activity.wota.WotaCsvReader;
import uk.m0nom.adifproc.activity.wwff.WwffCsvReader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.*;
import java.util.logging.Logger;

/**
 * This is the collection of all activity databases, including the readers to obtain the data from the source
 * files
 */
@Service
public class ActivityDatabaseService {
    private static final Logger logger = Logger.getLogger(ActivityDatabaseService.class.getName());

    private final Map<String, ActivityDatabase> databases;
    private final Map<ActivityType, ActivityReader> readers;

    /**
     * Constructor creates readers for all supported activities
     */
    public ActivityDatabaseService() {
        databases = new HashMap<>();
        readers = new HashMap<>();

        readers.put(ActivityType.HEMA, new HemaCsvReader("hema/HEMA-summits.csv"));
        readers.put(ActivityType.SOTA, new SotaCsvReader( "sota/summitslist.csv"));
        readers.put(ActivityType.GMA, new GmaCsvReader( "gma/gma_summits.csv"));
        readers.put(ActivityType.POTA, new PotaCsvReader("pota/all_parks_ext.csv"));
        readers.put(ActivityType.WOTA, new WotaCsvReader( "wota/summits.csv"));
        readers.put(ActivityType.WWFF, new WwffCsvReader("wwff/wwff_directory.csv"));
        readers.put(ActivityType.COTA, new CotaCsvReader("cota/cota.csv"));
        readers.put(ActivityType.LOTA, new LotaCsvReader("lota/lighthouses.csv"));
        readers.put(ActivityType.ROTA, new RotaCsvReader("rota/2022-rota.csv"));
        readers.put(ActivityType.IOTA, new IotaJsonReader("iota/iota-full-list.json"));
        readers.put(ActivityType.BOTA, new BotaCsvReader("bota/UKBOTA-Bunker-Reference-List.csv"));
    }

    /**
     * For each supported activity this calls the reader to load the activity data into a database and
     * then maintain a reference here using the ActivityType. Source files are read from the resources directory
     */
    public void loadData() {
        readers.values().forEach(reader -> {
                boolean readingFromRemote = false;
                InputStream inputStream = null;
                if (reader instanceof RemoteActivitySource remoteActivitySource) {
                    // Attempt to download the file from the remote source
                    try {
                        inputStream = URI.create(remoteActivitySource.getRemoteUrl()).toURL().openStream();
                        if (inputStream != null) {
                            logger.info(String.format("Loading %s from %s", reader.getType().getActivityName(), remoteActivitySource.getRemoteUrl()));
                            readingFromRemote = true;
                        }
                    } catch (IOException e) {
                        // problem downloading the file, so load the local copy instead
                        logger.warning(String.format("Problem downloading %s from %s", reader.getType().getActivityName(), remoteActivitySource.getRemoteUrl()));
                    }
                }
                if (!readingFromRemote) {
                    inputStream = getClass().getClassLoader().getResourceAsStream(reader.getSourceFile());
                    if (inputStream == null) {
                        logger.severe(String.format("Can't load %s using classloader %s", reader.getSourceFile(), getClass().getClassLoader().toString()));
                    }
                }

                try {
                    ActivityDatabase database = reader.read(inputStream);
                    databases.put(reader.getType().getActivityName(), database);
                } catch (IOException e) {
                    logger.severe(String.format("Exception thrown reading activity databases: %s", e.getMessage()));
                }

                //logger.info(String.format("%d %s records loaded", database.size(), reader.getType().getActivityDescription()));
            });
    }

    /**
     * Given an arbitrary activity reference, search each of the databases for a matching activity
     * @param reference String reference for the activity to search for
     * @return If a match is found the corresponding activity is returned, otherwise null
     */
    public Activity findActivity(String reference) {
        for (String activityType : databases.keySet()) {
            ActivityDatabase database = getDatabase(activityType);
            Activity activity = database.get(reference);
            if (activity != null) {
                return activity;
            }
        }
        return null;
    }

    /**
     * Given an activity type return the corresponding database
     * @param type Activity type
     * @return database for activity
     */
    public ActivityDatabase getDatabase(ActivityType type) {
        return databases.get(type.getActivityName());
    }

    /**
     * Get the database for the named activity based on the activityName field in each activity type
     * @param requested name of the activity type
     * @return database if activity type found, otherwise null
     */
    public ActivityDatabase getDatabase(String requested) {
       return databases.get(requested);
    }

    public Collection<Activity> findAllActivities() {
        List<Activity> allActivities = new ArrayList<>();
        for (ActivityDatabase database : databases.values()) {
            allActivities.addAll(database.getValues());
        }
        return allActivities;
    }
}
