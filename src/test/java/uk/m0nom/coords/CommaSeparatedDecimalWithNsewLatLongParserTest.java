package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CommaSeparatedDecimalWithNsewLatLongParserTest {
    @Test
    public void test() {
        String input = "49.6850503S, 13.0318131W";
        Double latitude = -49.6850503;
        Double longitude = -13.0318131;

        GlobalCoordinates coords = new CommaSeparatedDecimalWithNsewLatLongParser().parse(input);
        assertNotNull("Coords is null", coords);
        assertTrue(Math.abs(coords.getLatitude() - latitude) < 0.0001);
        assertTrue(Math.abs(coords.getLongitude() - longitude) < 0.0001);
    }
}
