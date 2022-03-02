package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CommaSeparatedDecimalLatLongParserTest {
    @Test
    public void test() {
        String input = "54.370985339290684,-2.9098945771236493";
        double latitude = 54.370985339290684;
        double longitude = -2.9098945771236493;

        GlobalCoordinates coords = new CommaSeparatedDecimalLatLongParser().parse(LocationSource.UNDEFINED, input);
        assertThat(coords).isNotNull();
        assertThat(Math.abs(coords.getLatitude() - latitude)).isLessThan(0.0001);
        assertThat(Math.abs(coords.getLongitude() - longitude)).isLessThan(0.0001);
    }
}
