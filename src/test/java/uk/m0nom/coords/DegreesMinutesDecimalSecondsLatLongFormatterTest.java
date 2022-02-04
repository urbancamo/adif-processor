package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Test;

import static org.junit.Assert.*;

public class DegreesMinutesDecimalSecondsLatLongFormatterTest {
    private final String DMS_STRING_1 = "43° 53' 37.700\"N 22° 17' 9.700\"E";
    private final String DMS_STRING_2 = "54° 27' 15.336\"N 3° 12' 42.048\"W";

    @Test
    public void test() {
        check(DMS_STRING_2, 54.454260, -3.211680);
        check(DMS_STRING_1, 43.893806, 22.286028);
    }

    private void check(String input, Double latitude, Double longitude) {
        DegreesMinutesDecimalSecondsWithNsewLatLongParser dms = new DegreesMinutesDecimalSecondsWithNsewLatLongParser();
        GlobalCoordinates coords = dms.parse(LocationSource.UNDEFINED, input);
        assertNotNull("Coords is null", coords);
        assertTrue(Math.abs(coords.getLatitude() - latitude) < 0.0001);
        assertTrue(Math.abs(coords.getLongitude() - longitude) < 0.0001);

        // Right now check the formatted output matches
        String formatted = dms.format(coords);
        assertEquals(String.format("Formatted String: %s doesn't match expected: %s", formatted, input), formatted, input);
    }
}
