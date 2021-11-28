package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class OsGb36Parser3DigitTest {
    @Test
    public void test() {
        check(" SD261849 ", 54.25479084403316, -3.1358811832581135);
    }

    private void check(String input, Double latitude, Double longitude) {
        GlobalCoordinates coords = new OsGb36Parser3Digit().parse(LocationSource.UNDEFINED, input);
        assertNotNull("Coords is null", coords);
        assertTrue((Math.abs(coords.getLatitude()) - Math.abs(latitude)) < 0.001);
        assertTrue((Math.abs(coords.getLongitude()) - Math.abs(longitude)) < 0.001);
    }
}
