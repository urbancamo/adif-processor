package uk.m0nom.adifproc.antenna;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class AntennaService {
    private final Map<String, Antenna> antennaMap;

    public AntennaService() {
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

        // Sloper
        Antenna sloper = new Antenna(AntennaType.SLOPER, "Sloper", 25);
        antennaMap.put(sloper.getName(), sloper);

        // YAGI
        Antenna yagi = new Antenna(AntennaType.YAGI, "YAGI", 10);
        antennaMap.put(yagi.getName(), yagi);
    }


    public Antenna getAntenna(String name) {
        return antennaMap.get(name);
    }

    public List<Antenna> getAntennas() {
        return antennaMap.values().stream().sorted().collect(Collectors.toList());
    }

    public List<String> getAntennaNames() {
        return antennaMap.values().stream().sorted().map(Antenna::getName).collect(Collectors.toList());
    }

}
