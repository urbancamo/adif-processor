package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Test;

import static org.junit.Assert.*;

public class DegreesMinutesDecimalSecondsWithNsewLatLongParserTest {
    @Test
    public void test1() {
        check("43°53'37.7\"N 22°17'09.7\"E)", 43.893806, 22.286028);
    }

    @Test
    public void test2() {
        check("54° 17' 20.43\" N 2 56' 14.82\" W)", 54.289008, -2.937450);
    }

    @Test
    public void testFormatter() {
        checkFormat("54° 17' 20.429\"N 2° 56' 14.820\"W", 54.289008, -2.937450);
    }

    private void check(String input, Double latitude, Double longitude) {
        GlobalCoordinates coords = new DegreesMinutesDecimalSecondsWithNsewLatLongParser().parse(LocationSource.UNDEFINED, input);
        assertNotNull("Coords is null", coords);
        assertTrue(Math.abs(coords.getLatitude() - latitude) < 0.0001);
        assertTrue(Math.abs(coords.getLongitude() - longitude) < 0.0001);
    }

    private void checkFormat(String expected, Double latitude, Double longitude) {
        GlobalCoordinates coords = new GlobalCoordinatesWithSourceAccuracy(latitude, longitude);
        String formatted = new DegreesMinutesDecimalSecondsWithNsewLatLongParser().format(coords);
        assertEquals(formatted, expected);
    }

}
