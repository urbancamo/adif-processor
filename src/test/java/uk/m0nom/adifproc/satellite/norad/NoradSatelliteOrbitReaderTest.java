package uk.m0nom.adifproc.satellite.norad;

import org.junit.jupiter.api.Test;
import uk.m0nom.adifproc.satellite.ApSatellites;

import static org.assertj.core.api.Assertions.assertThat;

public class NoradSatelliteOrbitReaderTest {
    @Test
    public void readTest() {
        ApSatellites apSatellites = new ApSatellites();
        assertThat(apSatellites.size()).isEqualTo(91);
    }
}
