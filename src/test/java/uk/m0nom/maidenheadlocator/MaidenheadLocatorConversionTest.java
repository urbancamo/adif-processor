package uk.m0nom.maidenheadlocator;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Test;

import static junit.framework.TestCase.assertEquals;

public class MaidenheadLocatorConversionTest {
    @Test
    public void testLocatorToLatLng6Char() {
        GlobalCoordinates ll = MaidenheadLocatorConversion.locatorToCoords("IO84ni");
        assertEquals("-2.875", String.format("%.3f", ll.getLongitude()));
        assertEquals("54.354", String.format("%.3f", ll.getLatitude()));
    }

    @Test
    public void testLocatorToLatLng10Char() {
        GlobalCoordinates ll = MaidenheadLocatorConversion.locatorToCoords("IO84mj91mb");
        assertEquals("-2.921", String.format("%.3f", ll.getLongitude()));
        assertEquals("54.379", String.format("%.3f", ll.getLatitude()));
    }
}
