package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommaSeparatedDecimalWithNsewLatLongParserTest {
    @Test
    public void test() {
        String input = "49.6850503S, 13.0318131W";
        double latitude = -49.6850503;
        double longitude = -13.0318131;

        GlobalCoordinates coords = new CommaSeparatedDecimalWithNsewLatLongParser().parse(LocationSource.UNDEFINED, input);
        assertThat(coords).isNotNull();
        assertThat(Math.abs(coords.getLatitude() - latitude)).isLessThan(0.0001);
        assertThat(Math.abs(coords.getLongitude() - longitude)).isLessThan(0.0001);
    }
}
