package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DegreesMinutesDecimalSecondsLatLongParserTest {
    @Test
    public void test() {
        check("43°53'37.7\"N 22°17'09.7\"E)", 43.893806, 22.286028);
    }

    private void check(String input, Double latitude, Double longitude) {
        GlobalCoordinates coords = new DegreesMinutesDecimalSecondsWithNsewLatLongParser().parse(LocationSource.UNDEFINED, input);
        assertNotNull("Coords is null", coords);
        assertTrue(Math.abs(coords.getLatitude() - latitude) < 0.0001);
        assertTrue(Math.abs(coords.getLongitude() - longitude) < 0.0001);
    }
}
