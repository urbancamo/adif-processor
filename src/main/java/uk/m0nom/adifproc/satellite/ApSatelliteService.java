package uk.m0nom.adifproc.satellite;

import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.satellite.norad.NoradSatelliteOrbitReader;
import uk.m0nom.adifproc.satellite.satellites.QO100;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Map of the satellites that the ADIF Processor supports, both LEO and Geostationary
 * Satellites can be identified either by their name or a code
 */
@Service
public class ApSatelliteService {

    private final NoradSatelliteOrbitReader noradSatelliteOrbitReader;

    private final ApSatellites satellites;

    public ApSatelliteService(NoradSatelliteOrbitReader noradSatelliteOrbitReader) {
        satellites = new ApSatellites();

        this.noradSatelliteOrbitReader = noradSatelliteOrbitReader;
        satellites.addOrReplace(new QO100(), null);
    }

    private void loadCurrentNoradSatelliteTleDataIfRequired() {
        if (!satellites.hasDataFor(LocalDate.now())) {
            noradSatelliteOrbitReader.loadCurrentSatelliteTleDataFromCelestrak(satellites);
        }
    }

    public ApSatellite getSatellite(String id, LocalDate date) {
        if (LocalDate.now().isEqual(date)) {
            loadCurrentNoradSatelliteTleDataIfRequired();
        } else if (!satellites.hasDataFor(date)) {
            noradSatelliteOrbitReader.loadTleDataFromArchive(satellites, date);
        }
        return satellites.get(id.toUpperCase());
    }

    public Collection<String> getSatelliteNames() {
        loadCurrentNoradSatelliteTleDataIfRequired();
        return satellites.getSatelliteNames();
    }

    public int getSatelliteCount() {
        loadCurrentNoradSatelliteTleDataIfRequired();
        return satellites.getSatelliteCount();
    }
}
