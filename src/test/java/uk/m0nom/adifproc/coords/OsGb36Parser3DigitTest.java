package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class OsGb36Parser3DigitTest {
    @Test
    public void testConvertOsGb36Parse3DigitToCoord() {
        check(" SD261849 ", 54.25479084403316, -3.1358811832581135);
    }

    @Test
    public void testCoordTo3DigitOsGb36() {
        GlobalCoordinates coords = new Maidenhead10CharLocatorParser().parse(LocationSource.UNDEFINED, "IO83PO38BM");
        String osGb36ThreeDigit = new OsGb36Parser3Digit().format(coords);
        assertThat(osGb36ThreeDigit).isEqualTo("SD 520 138");
    }

    private void check(String input, Double latitude, Double longitude) {
        GlobalCoordinates coords = new OsGb36Parser3Digit().parse(LocationSource.UNDEFINED, input);
        assertThat(coords).isNotNull();
        assertThat((Math.abs(coords.getLatitude()) - Math.abs(latitude))).isLessThan(0.001);
        assertThat((Math.abs(coords.getLongitude()) - Math.abs(longitude))).isLessThan(0.001);
    }
}
