package uk.m0nom.adifproc.satellite;

import org.springframework.stereotype.Component;
import uk.m0nom.adifproc.satellite.satellites.QO100;

import java.time.ZonedDateTime;
import java.util.*;

@Component
public class ApSatellites {
    private final Map<String, ApSatellite> satelliteMap;
    private final Map<String, String> satelliteDesignatorToNameMap;

    private final Set<ZonedDateTime> datesLoaded;

    public ApSatellites() {
        satelliteMap = new HashMap<>();
        satelliteDesignatorToNameMap = new HashMap<>();
        datesLoaded = new TreeSet<>();
        addOrReplace(new QO100(), null);
    }

    public void addOrReplace(ApSatellite satellite, ZonedDateTime date) {
        String name = satellite.getName();

        if (satelliteMap.get(name) != null) {
            satelliteMap.remove(name);
            satelliteDesignatorToNameMap.remove(satellite.getDesignator());
        }
        satelliteMap.put(name, satellite);
        satelliteDesignatorToNameMap.put(satellite.getDesignator(), satellite.getName());
        if (date != null) {
            datesLoaded.add(date);
        }
    }

    public boolean noDataFor(ZonedDateTime date) {
        return date != null && !datesLoaded.contains(date);
    }

    /**
     * Retrieve a satellite using the name, designator or alias
     * @param id may be either a name or designator
     * @return satellite if loaded with either the name or designator
     */
    public ApSatellite get(String id) {
        ApSatellite satellite = satelliteMap.get(id);
        if (satellite == null) {
            String name = satelliteDesignatorToNameMap.get(id);
            if (name != null) {
                satellite = satelliteMap.get(name);
            }
        }
        return satellite;
    }

    public Collection<String> getSatelliteNames() {
        return satelliteMap.keySet();
    }

    public int getSatelliteCount() {
        return satelliteMap.size();
    }
}
