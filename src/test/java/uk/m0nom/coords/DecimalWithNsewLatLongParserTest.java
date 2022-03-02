package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DecimalWithNsewLatLongParserTest {
    @Test
    public void test() {
        String input = "50.4490S 3.6366E";
        double latitude = -50.4490;
        double longitude = -3.6366;

        GlobalCoordinates coords = new DegreesDecimalWithNsewLatLongParser().parse(LocationSource.UNDEFINED, input);

        assertThat(coords).isNotNull();
        assertThat(Math.abs(coords.getLatitude()) - Math.abs(latitude)).isLessThan(0.001);
        assertThat(Math.abs(coords.getLongitude()) - Math.abs(longitude)).isLessThan(0.001);
    }
}
