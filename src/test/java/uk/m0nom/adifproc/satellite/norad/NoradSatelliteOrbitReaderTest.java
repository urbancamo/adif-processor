package uk.m0nom.adifproc.satellite.norad;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.m0nom.adifproc.FileProcessorApplicationConfig;
import uk.m0nom.adifproc.satellite.ApSatelliteService;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FileProcessorApplicationConfig.class)
public class NoradSatelliteOrbitReaderTest {
    @Autowired
    private ApSatelliteService apSatelliteService;

    // @Test
    // TODO norad blocks access to the TLE file if downloaded excessively, need a way to download
    // once per day and store in target or somewhere so that tests run from that file for the rest
    // of the day.
    public void readTest() {
        assertThat(apSatelliteService.getSatelliteCount()).isGreaterThan(89);
    }
}
