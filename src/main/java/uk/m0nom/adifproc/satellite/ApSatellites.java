package uk.m0nom.adifproc.satellite;

import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.satellite.satellites.QO100;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Map of the satellites that the ADIF Processor supports, both LEO and Geostationary
 * Satellites can be identified either by their name or a code
 */
@Service
public class ApSatellites {
    private static final Logger logger = Logger.getLogger(ApSatellites.class.getName());

    private final Map<String, ApSatellite> satelliteIdentifierMap = new HashMap<>();

    public ApSatellites() {
        QO100 qo100 = new QO100();
        satelliteIdentifierMap.put(qo100.getIdentifier(), qo100);
    }

    public ApSatellite getSatellite(String ident) {
        return satelliteIdentifierMap.get(ident);
    }

    public Collection<String> getSatelliteNames() {
        return satelliteIdentifierMap.keySet();
    }

    public int size() {
        return satelliteIdentifierMap.size();
    }
}
