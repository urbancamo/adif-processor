package uk.m0nom.antenna;

import java.util.*;

public class Antennas {
    private final Map<String, Antenna> antennaMap;

    public Antennas() {
        antennaMap = new HashMap<>();

        // Vertical
        Antenna vertical = new Antenna(AntennaType.VERTICAL, "Vertical", 15);
        antennaMap.put(vertical.getName(), vertical);

        // Dipole
        Antenna dipole = new Antenna(AntennaType.DIPOLE, "Dipole", 20);
        antennaMap.put(dipole.getName(), dipole);

        // Inverted-V
        Antenna invertedV = new Antenna(AntennaType.INV_V, "Inverted-V", 25);
        antennaMap.put(invertedV.getName(), invertedV);

        // YAGI
        Antenna yagi = new Antenna(AntennaType.YAGI, "YAGI", 10);
        antennaMap.put(yagi.getName(), yagi);
    }


    public Antenna getAntenna(String name) {
        return antennaMap.get(name);
    }

    public Set<String> getAntennaNames() {
        return antennaMap.keySet();
    }
}
