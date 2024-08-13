package uk.m0nom.adifproc.satellite;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.satellite.norad.NoradSatellite;
import uk.m0nom.adifproc.satellite.norad.NoradSatelliteOrbitReader;
import uk.m0nom.adifproc.satellite.satellites.SatelliteNameAliases;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collection;

/**
 * Map of the satellites that the ADIF Processor supports, both LEO and Geostationary
 * Satellites can be identified either by their name or a code
 */
@Service
@RequiredArgsConstructor
public class ApSatelliteService {

    private final NoradSatelliteOrbitReader noradSatelliteOrbitReader;

    private final SatelliteNameAliases satelliteNameAliases;

    private final ApSatellites satellites;

    @Getter
    private final ZonedDateTime earliestDataAvailable = ZonedDateTime.of(LocalDateTime.of(2022, 2, 23, 0, 0), ZoneId.of("UTC"));

    private void loadCurrentNoradSatelliteTleDataIfRequired() {
        if (satellites.noDataFor(ZonedDateTime.now())) {
            noradSatelliteOrbitReader.loadCurrentSatelliteTleDataFromCelestrak(satellites);
        }
    }

    public ApSatellite getSatellite(String id, ZonedDateTime date) {
        ApSatellite satellite = getSatellite(id);
        if (satellite == null || satellite instanceof NoradSatellite) {
            if (ZonedDateTime.now().isEqual(date)) {
                loadCurrentNoradSatelliteTleDataIfRequired();
            } else if (satellites.noDataFor(date)) {
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

}
