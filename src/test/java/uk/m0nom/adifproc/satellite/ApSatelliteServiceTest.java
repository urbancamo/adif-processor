package uk.m0nom.adifproc.satellite;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.m0nom.adifproc.FileProcessorApplicationConfig;
import uk.m0nom.adifproc.satellite.norad.NoradSatellite;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@ActiveProfiles("dev")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FileProcessorApplicationConfig.class)
public class ApSatelliteServiceTest {
    @Autowired
    ApSatelliteService apSatelliteService;

    @Test
    public void testHistorySatelliteReadUsingName() {
        LocalDate date = LocalDate.of(2022,4,2);

        ApSatellite satellite = apSatelliteService.getSatellite("JAS-2", date);
        assertThat(satellite).isNotNull();
        assertThat(satellite).isInstanceOf(NoradSatellite.class);
        NoradSatellite noradSatellite = (NoradSatellite) satellite;
        assertThat(noradSatellite.getSatelliteTleDataForDate(date)).isNotNull();
    }


    @Test
    public void testHistorySatelliteReadUsingDesignator() {
        LocalDate date = LocalDate.of(2022,4,1);

        ApSatellite satellite = apSatelliteService.getSatellite("FO-29", date);
        assertThat(satellite).isNotNull();
        assertThat(satellite).isInstanceOf(NoradSatellite.class);
        NoradSatellite noradSatellite = (NoradSatellite) satellite;
        assertThat(noradSatellite.getSatelliteTleDataForDate(date)).isNotNull();
    }

    @Test
    public void testHistorySatelliteReadUsingAlias() {
        LocalDate date = LocalDate.of(2022,4,1);

        ApSatellite satellite = apSatelliteService.getSatellite("ARISS", date);
        assertThat(satellite).isNotNull();
        assertThat(satellite).isInstanceOf(NoradSatellite.class);
        NoradSatellite noradSatellite = (NoradSatellite) satellite;
        assertThat(noradSatellite.getName()).isEqualTo("ISS");
        assertThat(noradSatellite.getSatelliteTleDataForDate(date)).isNotNull();

    }
}
