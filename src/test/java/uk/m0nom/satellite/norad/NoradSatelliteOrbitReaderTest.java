package uk.m0nom.satellite.norad;

import org.junit.Test;
import uk.m0nom.satellite.ApSatellites;

import static org.junit.Assert.assertEquals;

public class NoradSatelliteOrbitReaderTest {
    @Test
    public void readTest() {
        ApSatellites apSatellites = new ApSatellites();
        assertEquals(91, apSatellites.size());
    }
}
