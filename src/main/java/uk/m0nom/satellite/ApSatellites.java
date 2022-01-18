package uk.m0nom.satellite;

import uk.m0nom.satellite.norad.NoradSatelliteOrbitReader;
import uk.m0nom.satellite.satellites.QO100;

import java.util.*;
import java.util.logging.Logger;

public class ApSatellites {
    private static final Logger logger = Logger.getLogger(ApSatellites.class.getName());

    private final Map<String, ApSatellite> satelliteMap = new HashMap<>();

    public ApSatellites() {
        QO100 qo100 = new QO100();
        satelliteMap.put(qo100.getName(), qo100);

        // read from Norad
        NoradSatelliteOrbitReader reader = new NoradSatelliteOrbitReader();
        Map<String, ApSatellite> noradSats = reader.readSatellites(NoradSatelliteOrbitReader.NORAD_TLE_FILE_LOCATION);
        if (noradSats != null) {
            satelliteMap.putAll(noradSats);
        } else {
            logger.severe(String.format("Error reading from satellite file: %s", NoradSatelliteOrbitReader.NORAD_TLE_FILE_LOCATION));
        }
    }

    public ApSatellite getSatellite(String name) {
        return satelliteMap.get(name);
    }

    public Set<String> getSatelliteNames() {
        SortedSet<String> names = new TreeSet<>();
        names.addAll(satelliteMap.keySet());
        return names;
    }

    public void addSatellite(ApSatellite apSatellite) {
        satelliteMap.put(apSatellite.getName(), apSatellite);
    }

    public int size() {
        return satelliteMap.size();
    }
}
