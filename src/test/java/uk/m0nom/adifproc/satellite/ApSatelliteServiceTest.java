package uk.m0nom.adifproc.satellite;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.m0nom.adifproc.satellite.norad.NoradSatellite;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class ApSatelliteServiceTest {
    @Autowired
    ApSatelliteService apSatelliteService;

    private final ZonedDateTime firstOfApril2022 = ZonedDateTime.of(LocalDateTime.of(2022,4,1, 0, 0), ZoneId.of("UTC"));
    private final ZonedDateTime secondOfApril2022 = ZonedDateTime.of(LocalDateTime.of(2022,4,1, 0, 0), ZoneId.of("UTC"));

    @Test
    public void testHistorySatelliteReadUsingName() {

        ApSatellite satellite = apSatelliteService.getSatellite("JAS-2", secondOfApril2022);
        assertThat(satellite).isNotNull();
        assertThat(satellite).isInstanceOf(NoradSatellite.class);
        NoradSatellite noradSatellite = (NoradSatellite) satellite;
        assertThat(noradSatellite.getSatelliteTleDataForDate(secondOfApril2022)).isNotNull();
    }


    @Test
    public void testHistorySatelliteReadUsingDesignator() {
        ApSatellite satellite = apSatelliteService.getSatellite("FO-29", firstOfApril2022);
        assertThat(satellite).isNotNull();
        assertThat(satellite).isInstanceOf(NoradSatellite.class);
        NoradSatellite noradSatellite = (NoradSatellite) satellite;
        assertThat(noradSatellite.getSatelliteTleDataForDate(firstOfApril2022)).isNotNull();
    }

    @Test
    public void testHistorySatelliteReadUsingAlias() {
        ApSatellite satellite = apSatelliteService.getSatellite("ARISS", firstOfApril2022);
        assertThat(satellite).isNotNull();
        assertThat(satellite).isInstanceOf(NoradSatellite.class);
        NoradSatellite noradSatellite = (NoradSatellite) satellite;
        assertThat(noradSatellite.getName()).isEqualTo("ISS");
        assertThat(noradSatellite.getSatelliteTleDataForDate(firstOfApril2022)).isNotNull();
    }
}
