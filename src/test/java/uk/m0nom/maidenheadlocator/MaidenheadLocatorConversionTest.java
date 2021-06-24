package uk.m0nom.maidenheadlocator;

import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class MaidenheadLocatorConversionTest {
    @Test
    public void testLocatorToLatLng6Char() {
        LatLng ll = MaidenheadLocatorConversion.locatorToLatLng("IO84ni");
        assertEquals("-2.875", String.format("%.3f", ll.longitude));
        assertEquals("54.354", String.format("%.3f", ll.latitude));
    }

    @Test
    public void testLocatorToLatLng10Char() {
        LatLng ll = MaidenheadLocatorConversion.locatorToLatLng("IO84mj91mb");
        assertEquals("-2.921", String.format("%.3f", ll.longitude));
        assertEquals("54.379", String.format("%.3f", ll.latitude));
    }
}
