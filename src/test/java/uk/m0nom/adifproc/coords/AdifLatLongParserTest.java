package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AdifLatLongParserTest {
    private final static String GB2GW = "<MY_LAT:11>N054 07.716<MY_LON:11>W003 08.018";
    private final double LATITUDE = 54.128600000000006;
    private final double LONGITUDE = -3.1336333333333357;

    @Test
    public void testParse() {

        GlobalCoordinates coords = new AdifLatLongParser("MY_").parse(LocationSource.UNDEFINED, GB2GW);
        assertThat(coords).isNotNull();
        assertThat(Math.abs(coords.getLatitude() - LATITUDE)).isLessThan(0.0001);
        assertThat(Math.abs(coords.getLongitude() - LONGITUDE)).isLessThan(0.0001);
    }

    @Test
    public void testFormat() {
        GlobalCoordinates coords = new GlobalCoordinates(LATITUDE, LONGITUDE);
        String formatted = new AdifLatLongParser("MY_").format(coords);
        assertThat(formatted).isEqualTo(GB2GW);
    }
}
