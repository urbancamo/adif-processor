package uk.m0nom.satellite;

import uk.m0nom.satellite.satellites.QO100;

import java.util.HashMap;
import java.util.Map;

public class Satellites {

    private Map<String, Satellite> satelliteMap = new HashMap<>();

    public Satellites() {
        QO100 qo100 = new QO100();
        satelliteMap.put(qo100.getName(), qo100);
    }

    public Satellite getSatellite(String name) {
        return satelliteMap.get(name);
    }
}
