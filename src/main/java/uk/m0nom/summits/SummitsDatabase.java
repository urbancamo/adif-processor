package uk.m0nom.summits;

import uk.m0nom.adif3.FileTransformerApp;
import uk.m0nom.sota.SotaCsvReader;
import uk.m0nom.sota.SotaSummitsDatabase;
import uk.m0nom.wota.WotaCsvReader;
import uk.m0nom.wota.WotaSummitsDatabase;

import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Logger;

public class SummitsDatabase {
    private static final Logger logger = Logger.getLogger(SummitsDatabase.class.getName());

    private WotaSummitsDatabase wota;
    private SotaSummitsDatabase sota;

    public void loadData() {
        try {
            String sotaSummitsList = "sota/summitslist.csv";
            InputStream sotaCsvStream = getClass().getClassLoader().getResourceAsStream(sotaSummitsList);
            if (sotaCsvStream == null) {
                logger.severe(String.format("Can't load %s using classloader %s", sotaSummitsList, getClass().getClassLoader().toString()));
            }
            logger.info(String.format("Loading SOTA Summits list from: %s", sotaSummitsList));
            setSota(SotaCsvReader.read(sotaCsvStream));
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
            setWota(WotaCsvReader.read(wotaCsvStream));
            logger.info(String.format("%d WOTA Summits loaded", getWota().size()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public WotaSummitsDatabase getWota() {
        return wota;
    }

    public void setWota(WotaSummitsDatabase wota) {
        this.wota = wota;
    }

    public SotaSummitsDatabase getSota() {
        return sota;
    }

    public void setSota(SotaSummitsDatabase sota) {
        this.sota = sota;
    }
}
