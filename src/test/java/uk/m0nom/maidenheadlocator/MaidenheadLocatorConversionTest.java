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
    public void testLocatorToLatLng10Char1() {
        LatLng ll = MaidenheadLocatorConversion.locatorToLatLng("IO84mk33mp");
        assertEquals("-2.971", String.format("%.3f", ll.longitude));
        assertEquals("54.432", String.format("%.3f", ll.latitude));
    }

    @Test
    public void testLocatorToLatLng10Char2() {
        LatLng ll = MaidenheadLocatorConversion.locatorToLatLng("IO84ld28ss");
        assertEquals("-3.060", String.format("%.3f", ll.longitude));
        assertEquals("54.162", String.format("%.3f", ll.latitude));
    }
}
