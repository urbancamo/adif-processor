package uk.m0nom.adifproc.satellite.norad;

import com.github.amsacode.predict4java.Satellite;
import com.github.amsacode.predict4java.SatelliteFactory;
import com.github.amsacode.predict4java.TLE;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.file.InternalFileService;
import uk.m0nom.adifproc.satellite.ApSatellites;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Logger;

/**
 * Reads a set of TLE definitions of satellite orbits from a NASA format TLE file.
 * See <a href="http://www.celestrak.com">http://www.celestrak.com</a> for more info on this format.
 * <p>
 * DECODE 2-LINE ELSETS WITH THE FOLLOWING KEY:
 * 1 AAAAAU 00  0  0 BBBBB.BBBBBBBB  .CCCCCCCC  00000-0  00000-0 0  DDDZ
 * 2 AAAAA EEE.EEEE FFF.FFFF GGGGGGG HHH.HHHH III.IIII JJ.JJJJJJJJKKKKKZ
 * KEY: A-CATALOGNUM B-EPOCHTIME C-DECAY D-ELSETNUM E-INCLINATION F-RAAN
 * G-ECCENTRICITY H-ARGPERIGEE I-MNANOM J-MNMOTION K-ORBITNUM Z-CHECKSUM
 */
@Service
public class NoradSatelliteOrbitReader {
    public final static String NORAD_TLE_FILE_LOCATION = "http://www.celestrak.com/NORAD/elements/amateur.txt";
    private final static String NORAD_FOLDER = "norad";

    private static final Logger logger = Logger.getLogger(NoradSatelliteOrbitReader.class.getName());

    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final InternalFileService internalFileService;

    public NoradSatelliteOrbitReader(@Qualifier("localInternalFileService") InternalFileService internalFileService) {
        this.internalFileService = internalFileService;
    }

    public void loadTleDataFromArchive(ApSatellites satellites, ZonedDateTime date) {
        String filename = String.format("%s-amateur.txt", dateFormatter.format(date));
        String tleDefinitions = internalFileService.readFile(NORAD_FOLDER, filename);
        if (tleDefinitions != null) {
            parseTleData(satellites, tleDefinitions, date);
        }
    }

    public void loadCurrentSatelliteTleDataFromCelestrak(ApSatellites satellites) {
        logger.info(String.format("Reading NORAD Satellite Definition File: %s", NORAD_TLE_FILE_LOCATION));
        try {
            String tleDefinitions = readTleDataFromCelestrak();
            parseTleData(satellites, tleDefinitions, ZonedDateTime.now());
        } catch (Exception e) {
            logger.severe(String.format("Unable to read TLE definition file: %s", NORAD_TLE_FILE_LOCATION));
        }
    }

    private void parseTleData(ApSatellites satellites, String tleDefinitions, ZonedDateTime onDate) {

        // Each TLE definition consists of three lines of text, so we parse the TLE file 3 lines at a time
        String[] tleLines = tleDefinitions.split("\\n");
        int i = 0;

        while (i < tleLines.length / 3) {
            String[] lines = new String[3];
            lines[0] = tleLines[(i * 3)];
            lines[1] = tleLines[(i * 3) + 1];
            lines[2] = tleLines[(i * 3) + 2];

            TLE tle = new TLE(lines);

            Satellite satellite = SatelliteFactory.createSatellite(tle);
            NoradSatellite noradSatellite = (NoradSatellite) satellites.get(NoradSatellite.getIdentifier(satellite.getTLE().getName()));
            if (noradSatellite == null) {
                noradSatellite = new NoradSatellite(onDate, satellite);
            } else {
                noradSatellite.addTleData(onDate, satellite);
            }
            satellites.addOrReplace(noradSatellite, onDate);
            i++;
        }
        logger.info(String.format("Read %d satellite definitions", i));
   }

    private String readTleDataFromCelestrak() throws IOException, URISyntaxException {
        URL u = new URI(NoradSatelliteOrbitReader.NORAD_TLE_FILE_LOCATION).toURL();
        try (InputStream in = u.openStream()) {
            return new String(in.readAllBytes(), StandardCharsets.US_ASCII);
        }
    }
}
