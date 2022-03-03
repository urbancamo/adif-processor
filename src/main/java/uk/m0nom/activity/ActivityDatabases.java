package uk.m0nom.activity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.cota.CotaCsvReader;
import uk.m0nom.activity.gma.GmaCsvReader;
import uk.m0nom.activity.hema.HemaCsvReader;
import uk.m0nom.activity.iota.IotaJsonReader;
import uk.m0nom.activity.lota.LotaCsvReader;
import uk.m0nom.activity.pota.PotaCsvReader;
import uk.m0nom.activity.rota.RotaCsvReader;
import uk.m0nom.activity.sota.SotaCsvReader;
import uk.m0nom.activity.wota.WotaCsvReader;
import uk.m0nom.activity.wwff.WwffCsvReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * This is the collection of all activity databases, including the readers to obtain the data from the source
 * files
 */
@Getter
@Setter
public class ActivityDatabases {
    private static final Logger logger = Logger.getLogger(ActivityDatabases.class.getName());

    private Map<ActivityType, ActivityDatabase> databases;
    private Map<ActivityType, ActivityReader> readers;

    /**
     * Constructor creates readers for all supported activities
     */
    public ActivityDatabases() {
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
        readers.put(ActivityType.ROTA, new RotaCsvReader("rota/2021-rota.csv"));
        readers.put(ActivityType.IOTA, new IotaJsonReader("iota/iota-full-list.json"));
    }

    /**
     * For each supported activity this calls the reader to load the activity data into a database and
     * then maintain a reference here using the ActivityType. Source files are read from the resources directory
     */
    public void loadData() {
        for (ActivityReader reader : readers.values()) {
            try {
                InputStream inputStream = getClass().getClassLoader().getResourceAsStream(reader.getSourceFile());
                if (inputStream == null) {
                    logger.severe(String.format("Can't load %s using classloader %s", reader.getSourceFile(), getClass().getClassLoader().toString()));
                }
                //logger.info(String.format("Loading %s data from: %s", reader.getType().getActivityDescription(), reader.getSourceFile()));
                ActivityDatabase database = reader.read(inputStream);
                databases.put(reader.getType(), database);
                //logger.info(String.format("%d %s records loaded", database.size(), reader.getType().getActivityDescription()));
            } catch (IOException e) {
                logger.severe(String.format("Exception thrown reading activity databases: %s", e.getMessage()));
            }
        }
    }

    public ActivityDatabase getDatabase(ActivityType type) {
        return databases.get(type);
    }

    /**
     * Given an arbitrary activity reference, search each of the databases for a matching activity
     * @param reference String reference for the activity to search for
     * @return If a match is found the corresponding activity is returned, otherwise null
     */
    public Activity findActivity(String reference) {
        for (ActivityType activityType : databases.keySet()) {
            ActivityDatabase database = getDatabase(activityType);
            Activity activity = database.get(reference);
            if (activity != null) {
                return activity;
            }
        }
        return null;
    }

    /**
     * Get the database for the named activity based on the activityName field in each activity type
     * @param requested name of the activity type
     * @return database if activity type found, otherwise null
     */
    public ActivityDatabase getDatabase(String requested) {
        for (ActivityType type : databases.keySet()) {
            if (StringUtils.equals(requested, type.getActivityName())) {
                return databases.get(type);
            }
        }
        return null;
    }
}
