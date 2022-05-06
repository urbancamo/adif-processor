package uk.m0nom.adifproc.satellite;

import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.satellite.norad.NoradSatellite;
import uk.m0nom.adifproc.satellite.norad.NoradSatelliteOrbitReader;
import uk.m0nom.adifproc.satellite.satellites.QO100;
import uk.m0nom.adifproc.satellite.satellites.SatelliteNameAliases;

import java.time.LocalDate;
import java.util.Collection;

/**
 * Map of the satellites that the ADIF Processor supports, both LEO and Geostationary
 * Satellites can be identified either by their name or a code
 */
@Service
public class ApSatelliteService {

    private final NoradSatelliteOrbitReader noradSatelliteOrbitReader;

    private final SatelliteNameAliases satelliteNameAliases;

    private final ApSatellites satellites;

    private final LocalDate earliestDataAvailable = LocalDate.of(2022, 02, 23);

    public ApSatelliteService(NoradSatelliteOrbitReader noradSatelliteOrbitReader, SatelliteNameAliases satelliteNameAliases) {
        satellites = new ApSatellites();
        this.satelliteNameAliases = satelliteNameAliases;

        this.noradSatelliteOrbitReader = noradSatelliteOrbitReader;
        satellites.addOrReplace(new QO100(), null);
    }

    private void loadCurrentNoradSatelliteTleDataIfRequired() {
        if (!satellites.hasDataFor(LocalDate.now())) {
            noradSatelliteOrbitReader.loadCurrentSatelliteTleDataFromCelestrak(satellites);
        }
    }

    public ApSatellite getSatellite(String id, LocalDate date) {
        ApSatellite satellite = getSatellite(id);
        if (satellite == null || satellite instanceof NoradSatellite) {
            if (LocalDate.now().isEqual(date)) {
                loadCurrentNoradSatelliteTleDataIfRequired();
            } else if (!satellites.hasDataFor(date)) {
                noradSatelliteOrbitReader.loadTleDataFromArchive(satellites, date);
            }
            satellite = getSatellite(id);
        }
        return satellite;
    }

    public ApSatellite getSatellite(String id) {
        return getSatelliteByNameIdOrAlias(id);
    }

    public ApSatellite getSatelliteByNameIdOrAlias(String id) {
        String satName = satelliteNameAliases.getSatelliteName(id.toUpperCase());
        if (satName == null) {
            satName = id.toUpperCase();
        }
        return satellites.get(satName);
    }


     public boolean isAKnownSatellite(String id) {
        return getSatellite(id) != null;
     }

    public Collection<String> getSatelliteNames() {
        loadCurrentNoradSatelliteTleDataIfRequired();
        return satellites.getSatelliteNames();
    }

    public int getSatelliteCount() {
        loadCurrentNoradSatelliteTleDataIfRequired();
        return satellites.getSatelliteCount();
    }

    public LocalDate getEarliestDataAvailable() { return earliestDataAvailable; }
}
