package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class CommaSeparatedDecimalLatLongParserTest {
    @Test
    public void test() {
        String input = "54.370985339290684,-2.9098945771236493";
        Double latitude = 54.370985339290684;
        Double longitude = -2.9098945771236493;

        GlobalCoordinates coords = new CommaSeparatedDecimalLatLongParser().parse(input);
        assertNotNull("Coords is null", coords);
        assertTrue(Math.abs(coords.getLatitude() - latitude) < 0.0001);
        assertTrue(Math.abs(coords.getLongitude() - longitude) < 0.0001);
    }
}
