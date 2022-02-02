package uk.m0nom.satellite;

import uk.m0nom.satellite.norad.NoradSatelliteOrbitReader;
import uk.m0nom.satellite.satellites.QO100;

import java.util.*;
import java.util.logging.Logger;

public class ApSatellites {
    private static final Logger logger = Logger.getLogger(ApSatellites.class.getName());

    private final Map<String, ApSatellite> satelliteIdentifierMap = new HashMap<>();

    public ApSatellites() {
        QO100 qo100 = new QO100();
        satelliteIdentifierMap.put(qo100.getIdentifier(), qo100);

        // read from Norad
        NoradSatelliteOrbitReader reader = new NoradSatelliteOrbitReader();
        Map<String, ApSatellite> noradSats = reader.readSatellites(NoradSatelliteOrbitReader.NORAD_TLE_FILE_LOCATION);
        if (noradSats != null) {
            for (ApSatellite noradSat: noradSats.values()) {
                satelliteIdentifierMap.put(noradSat.getIdentifier(), noradSat);
            }
        } else {
            logger.severe(String.format("Error reading from satellite file: %s", NoradSatelliteOrbitReader.NORAD_TLE_FILE_LOCATION));
        }
    }

    public ApSatellite getSatellite(String ident) {
        for (String identifier : satelliteIdentifierMap.keySet()) {
            if (identifier.toUpperCase().contains(ident.toUpperCase())) {
                return satelliteIdentifierMap.get(identifier);
            }
        }
        return null;
    }

    public Set<String> getSatelliteNames() {
        SortedSet<String> names = new TreeSet<>();
        names.addAll(satelliteIdentifierMap.keySet());
        return names;
    }

    public void addSatellite(ApSatellite apSatellite) {
        satelliteIdentifierMap.put(apSatellite.getName(), apSatellite);
    }

    public int size() {
        return satelliteIdentifierMap.size();
    }
}
