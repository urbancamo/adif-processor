package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OsGb36Parser3DigitTest {
    @Test
    public void test() {
        check(" SD261849 ", 54.25479084403316, -3.1358811832581135);
    }

    private void check(String input, Double latitude, Double longitude) {
        GlobalCoordinates coords = new OsGb36Parser3Digit().parse(LocationSource.UNDEFINED, input);
        assertThat(coords).isNotNull();
        assertThat((Math.abs(coords.getLatitude()) - Math.abs(latitude))).isLessThan(0.001);
        assertThat((Math.abs(coords.getLongitude()) - Math.abs(longitude))).isLessThan(0.001);
    }
}
