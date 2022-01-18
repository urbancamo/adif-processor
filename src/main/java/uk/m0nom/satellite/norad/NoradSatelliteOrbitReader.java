package uk.m0nom.satellite.norad;

import com.fasterxml.jackson.databind.DatabindException;
import com.github.amsacode.predict4java.Satellite;
import com.github.amsacode.predict4java.SatelliteFactory;
import com.github.amsacode.predict4java.TLE;
import uk.m0nom.satellite.ApSatellite;
import uk.m0nom.satellite.ApSatellites;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

public class NoradSatelliteOrbitReader {
    public final static String NORAD_TLE_FILE_LOCATION = "http://www.celestrak.com/NORAD/elements/amateur.txt";
    private static final Logger logger = Logger.getLogger(NoradSatelliteOrbitReader.class.getName());

    private static final Map<String, ApSatellite> cache = new HashMap<>();
    private static long lastRead = 0L;
    private static final long CACHE_EXPIRES_MILLIS = 1000L * 60L * 60L * 24L;

    public Map<String, ApSatellite> readSatellites(String sourceFileUrl)
    {
        if (isCacheCurrent()) {
            logger.info(String.format("Returning %d cached satellites", cache.size()));
            return cache;
        } else {
            cache.clear();
            logger.info(String.format("Reading NORAD Satellite Definition File: %s", sourceFileUrl));
            try {
                String tleDefinitions = readUrl(sourceFileUrl);

                // Each TLE definition consists of three lines of text, so we parse the TLE file 3 lines at a time
                String tleLines[] = tleDefinitions.split("\\n");
                int i = 0;

                while (i < tleLines.length / 3) {
                    String lines[] = new String[3];
                    lines[0] = tleLines[(i * 3) + 0];
                    lines[1] = tleLines[(i * 3) + 1];
                    lines[2] = tleLines[(i * 3) + 2];

                    TLE tle = new TLE(lines);

                    Satellite satellite = SatelliteFactory.createSatellite(tle);
                    ApSatellite apSatellite = new NoradSatellite(satellite);
                    cache.put(apSatellite.getName(), apSatellite);
                    i++;
                }
                logger.info(String.format("Read %d satellite definitions", i));
                lastRead = new Date().getTime();

                return cache;
            } catch (IOException e) {
                logger.severe(String.format("Unable to read TLE definition file: %s", NORAD_TLE_FILE_LOCATION));
            }
        }
        return null;
    }

    private boolean isCacheCurrent() {
        long millisSinceEpoch = new Date().getTime();
        return millisSinceEpoch - lastRead < CACHE_EXPIRES_MILLIS;
    }

    private String readUrl(String tleDefinitionFile) throws IOException {
        URL u = new URL(tleDefinitionFile);
        try (InputStream in = u.openStream()) {
            return new String(in.readAllBytes(), StandardCharsets.US_ASCII);
        }
    }
}
