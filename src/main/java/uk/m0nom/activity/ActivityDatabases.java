package uk.m0nom.activity;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import uk.m0nom.activity.cota.CotaCsvReader;
import uk.m0nom.activity.pota.PotaCsvReader;
import uk.m0nom.activity.hema.HemaCsvReader;
import uk.m0nom.activity.sota.SotaCsvReader;
import uk.m0nom.activity.wota.WotaCsvReader;
import uk.m0nom.activity.wwff.WwffCsvReader;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

@Getter
@Setter
public class ActivityDatabases {
    private static final Logger logger = Logger.getLogger(ActivityDatabases.class.getName());

    private Map<ActivityType, ActivityDatabase> databases;
    private Map<ActivityType, ActivityReader> readers;

    public ActivityDatabases() {
        databases = new HashMap<>();
        readers = new HashMap<>();

        readers.put(ActivityType.HEMA, new HemaCsvReader("hema/HEMA-summits.csv"));
        readers.put(ActivityType.SOTA, new SotaCsvReader( "sota/summitslist.csv"));
        readers.put(ActivityType.POTA, new PotaCsvReader("pota/all_parks_ext.csv"));
        readers.put(ActivityType.WOTA, new WotaCsvReader( "wota/summits.csv"));
        readers.put(ActivityType.WWFF, new WwffCsvReader("wwff/wwff_directory.csv"));
        readers.put(ActivityType.COTA, new CotaCsvReader("cota/cota.csv"));
    }

    public void loadData() {
        for (ActivityReader reader : readers.values()) {
            try {
                InputStream csvStream = getClass().getClassLoader().getResourceAsStream(reader.getSourceFile());
                if (csvStream == null) {
                    logger.severe(String.format("Can't load %s using classloader %s", reader.getSourceFile(), getClass().getClassLoader().toString()));
                }
                logger.info(String.format("Loading %s data from: %s", reader.getType().getActivityDescription(), reader.getSourceFile()));
                ActivityDatabase database = reader.read(csvStream);
                databases.put(reader.getType(), database);
                logger.info(String.format("%d %s records loaded", database.size(), reader.getType().getActivityName()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public ActivityDatabase getDatabase(ActivityType type) {
        return databases.get(type);
    }

    public ActivityDatabase getDatabase(String requested) {
        for (ActivityType type : databases.keySet()) {
            if (StringUtils.equals(requested, type.getActivityName())) {
                return databases.get(type);
            }
        }
        return null;
    }
}
