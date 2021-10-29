package uk.m0nom.satellite;

import uk.m0nom.satellite.satellites.QO100;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class Satellites {

    private final Map<String, Satellite> satelliteMap = new HashMap<>();

    public Satellites() {
        QO100 qo100 = new QO100();
        satelliteMap.put(qo100.getName(), qo100);
    }

    public Satellite getSatellite(String name) {
        return satelliteMap.get(name);
    }

    public Set<String> getSatelliteNames() {
        return satelliteMap.keySet();
    }
}
