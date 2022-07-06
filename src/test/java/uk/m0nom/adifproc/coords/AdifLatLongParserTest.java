package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class AdifLatLongParserTest {
    private final static String GB2GW = "<MY_LAT:11>N054 07.716<MY_LON:11>W003 08.018";
    private final static String SPACE_COORDS = "   <   LAT:11 >    N052 18.467 \n\r   <   LON:11 >   E000 03.500  ";
    private final double LATITUDE1 = 54.128600000000006;
    private final double LONGITUDE1 = -3.1336333333333357;

    private final double LATITUDE2 = 52.307783;
    private final double LONGITUDE2 = 0.058333;

    @Test
    public void testParseSpacedCrLfString() {
        GlobalCoordinates coords = new AdifLatLongParser("").parse(LocationSource.UNDEFINED, SPACE_COORDS);
        assertThat(coords).isNotNull();
        assertThat(Math.abs(coords.getLatitude() - LATITUDE2)).isLessThan(0.0001);
        assertThat(Math.abs(coords.getLongitude() - LONGITUDE2)).isLessThan(0.0001);
    }

    @Test
    public void testParseGb2gw() {
        GlobalCoordinates coords = new AdifLatLongParser("MY_").parse(LocationSource.UNDEFINED, GB2GW);
        assertThat(coords).isNotNull();
        assertThat(Math.abs(coords.getLatitude() - LATITUDE1)).isLessThan(0.0001);
        assertThat(Math.abs(coords.getLongitude() - LONGITUDE1)).isLessThan(0.0001);
    }

    @Test
    public void testFormatGb2gw() {
        GlobalCoordinates coords = new GlobalCoordinates(LATITUDE1, LONGITUDE1);
        String formatted = new AdifLatLongParser("MY_").format(coords);
        assertThat(formatted).isEqualTo(GB2GW);
    }
}
