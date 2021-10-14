package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DecimalWithNsewLatLongParserTest {
    @Test
    public void test() {
        String input = "50.4490S 3.6366E";
        double latitude = -50.4490;
        double longitude = -3.6366;

        GlobalCoordinates coords = new DecimalWithNsewLatLongParser().parse(LocationSource.UNDEFINED, input);
        assertNotNull("Coords is null", coords);
        assertTrue(Math.abs(coords.getLatitude()) - Math.abs(latitude) < 0.001);
        assertTrue(Math.abs(coords.getLongitude()) - Math.abs(longitude) < 0.001);
    }
}
