package uk.m0nom.activity;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.activity.pota.PotaCsvReader;
import uk.m0nom.activity.pota.PotaDatabase;
import uk.m0nom.activity.hema.HemaCsvReader;
import uk.m0nom.activity.hema.HemaSummitsDatabase;
import uk.m0nom.activity.sota.SotaCsvReader;
import uk.m0nom.activity.sota.SotaSummitsDatabase;
import uk.m0nom.activity.wota.WotaCsvReader;
import uk.m0nom.activity.wota.WotaSummitsDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

@Getter
@Setter
public class ActivityDatabase {
    private static final Logger logger = Logger.getLogger(ActivityDatabase.class.getName());

    private WotaSummitsDatabase wota;
    private SotaSummitsDatabase sota;
    private HemaSummitsDatabase hema;
    private PotaDatabase pota;
    
    public void loadData() {
        try {
            String sotaSummitsList = "sota/summitslist.csv";
            InputStream sotaCsvStream = getClass().getClassLoader().getResourceAsStream(sotaSummitsList);
            if (sotaCsvStream == null) {
                logger.severe(String.format("Can't load %s using classloader %s", sotaSummitsList, getClass().getClassLoader().toString()));
            }
            logger.info(String.format("Loading SOTA Summits list from: %s", sotaSummitsList));
            setSota(new SotaCsvReader().read(sotaCsvStream));
            logger.info(String.format("%d SOTA Summits loaded", getSota().size()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {

            String wotaSummitsList = "wota/summits.csv";
            InputStream wotaCsvStream = getClass().getClassLoader().getResourceAsStream(wotaSummitsList);
            if (wotaCsvStream == null) {
                logger.severe(String.format("Can't load %s using classloader %s", wotaSummitsList, getClass().getClassLoader().toString()));
            }
            logger.info(String.format("Loading WOTA Summits list from: %s", wotaSummitsList));
            setWota(new WotaCsvReader().read(wotaCsvStream));
            logger.info(String.format("%d WOTA Summits loaded", getWota().size()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String hemaSummitsList = "hema/HEMA-summits.csv";
            InputStream hemaCsvStream = getClass().getClassLoader().getResourceAsStream(hemaSummitsList);
            if (hemaCsvStream == null) {
                logger.severe(String.format("Can't load %s using classloader %s", hemaSummitsList, getClass().getClassLoader().toString()));
            }
            logger.info(String.format("Loading HEMA Summits list from: %s", hemaSummitsList));
            setHema(new HemaCsvReader().read(hemaCsvStream));
            logger.info(String.format("%d HEMA Summits loaded", getHema().size()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            String potaList = "pota/all_parks_ext.csv";
            InputStream potaCsvStream = getClass().getClassLoader().getResourceAsStream(potaList);
            if (potaCsvStream == null) {
                logger.severe(String.format("Can't load %s using classloader %s", potaList, getClass().getClassLoader().toString()));
            }
            logger.info(String.format("Loading Parks on the Air list from: %s", potaList));
            setPota(new PotaCsvReader().read(potaCsvStream));
            logger.info(String.format("%d Parks loaded", getPota().size()));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
