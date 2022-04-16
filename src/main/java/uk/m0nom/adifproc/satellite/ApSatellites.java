package uk.m0nom.adifproc.satellite;

import java.time.LocalDate;
import java.util.*;

public class ApSatellites {
    private final Map<String, ApSatellite> satelliteMap;
    private final Map<String, String> satelliteDesignatorToNameMap;

    private final Set<LocalDate> datesLoaded;

    public ApSatellites() {
        satelliteMap = new HashMap<>();
        satelliteDesignatorToNameMap = new HashMap<>();
        datesLoaded = new TreeSet<>();
    }

    public void addOrReplace(ApSatellite satellite, LocalDate date) {
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

    public boolean hasDataFor(LocalDate date) {
        return date == null || datesLoaded.contains(date);
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
