package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DegreesDecimalMinutesLatLongParserTest {
    @Test
    public void test() {
        check("48°28.510\"N 14°11.310\"E", 48.475167, 14.1885);
    }

    private void check(String input, Double latitude, Double longitude) {
        GlobalCoordinates coords = new DegreesDecimalMinutesLatLongParser().parse(input);
        assertNotNull("Coords is null", coords);
        assertTrue(Math.abs(coords.getLatitude()) - Math.abs(latitude) < 0.001);
        assertTrue(Math.abs(coords.getLongitude()) - Math.abs(longitude) < 0.001);
    }
}
