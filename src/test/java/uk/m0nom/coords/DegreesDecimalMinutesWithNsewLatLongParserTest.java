package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class DegreesDecimalMinutesWithNsewLatLongParserTest {
    @Test
    public void test1() {
        check("48°28.510'N 14°11.310'E", 48.475167, 14.1885);
    }

    @Test
    public void test2() {
        check("51°53.31' N 0°32.77' W", 51.888464861082646, -0.5462438363385727);
    }

    @Test
    public void test3() {
        check("51°53.31'N 0°32.77'W", 51.888464861082646, -0.5462438363385727);
    }

    @Test
    public void test4() {
        check("51 53.31'N 0 32.77'W", 51.888464861082646, -0.5462438363385727);
    }

    private void check(String input, Double latitude, Double longitude) {
        GlobalCoordinates coords = new DegreesDecimalMinutesWithNsewLatLongParser().parse(LocationSource.UNDEFINED, input);
        assertNotNull("Coords is null", coords);
        assertTrue(Math.abs(coords.getLatitude()) - Math.abs(latitude) < 0.001);
        assertTrue(Math.abs(coords.getLongitude()) - Math.abs(longitude) < 0.001);
    }
}