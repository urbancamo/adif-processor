package uk.m0nom.maidenheadlocator;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class MaidenheadLocatorConversionTest {
    @Test
    public void testLocatorToLatLng() {
        LatLng ll = MaidenheadLocatorConversion.locatorToLatLng("IO84ni");
        assertEquals(String.format("%.3f", ll.longitude), "-2.875");
        assertEquals(String.format("%.5f", ll.latitude), "54.35417");
    }
}
