package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class DegreesMinutesSecondsLatLongParserTest {
    @Test
    public void test() {
        check("24°12?58?N 55°45?9?E", 24.216111, 55.7525);
        check("24°12?58?s 55°45?9?w", -24.216111, -55.7525);
        check("59°02?43? N 24°27?08? E", 59.045278, 24.452222);
    }

    private void check(String input, Double latitude, Double longitude) {
        GlobalCoordinates coords = new DegreesMinutesSecondsWithNsewLatLongParser().parse(LocationSource.UNDEFINED, input);
        assertThat(coords).isNotNull();
        assertThat(Math.abs(coords.getLatitude() - latitude)).isLessThan(0.0001);
        assertThat(Math.abs(coords.getLongitude() - longitude)).isLessThan(0.0001);
    }
}
