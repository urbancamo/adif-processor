package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DegreesMinutesSecondsLatLongParserTest {
    @Test
    public void test() {
        check("24°12?58?N 55°45?9?E", 24.216111, 55.7525);
        check("24°12?58?s 55°45?9?w", -24.216111, -55.7525);
        check("59°02?43? N 24°27?08? E", 59.045278, 24.452222);
    }

    private void check(String input, Double latitude, Double longitude) {
        GlobalCoordinates coords = new DegreesMinutesSecondsLatLongParser().parse(input);
        assertNotNull("Coords is null", coords);
        assertTrue(Math.abs(coords.getLatitude() - latitude) < 0.0001);
        assertTrue(Math.abs(coords.getLongitude() - longitude) < 0.0001);
    }
}
