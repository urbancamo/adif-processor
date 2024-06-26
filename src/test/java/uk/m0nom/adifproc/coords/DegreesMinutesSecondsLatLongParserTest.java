package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DegreesMinutesSecondsLatLongParserTest {
    @Test
    public void test() {
        check("54° 17' 18\", -2° 56' 16\"", 54.28833333333333, -2.937777777777768);
    }

    private void check(String input, Double latitude, Double longitude) {
        GlobalCoordinates coords = new DegreesMinutesSecondsLatLongParser().parse(LocationSource.UNDEFINED, input);
        assertThat(coords).isNotNull();
        assertThat(Math.abs(coords.getLatitude() - latitude)).isLessThan(0.0001);
        assertThat(Math.abs(coords.getLongitude() - longitude)).isLessThan(0.0001);
    }
}
