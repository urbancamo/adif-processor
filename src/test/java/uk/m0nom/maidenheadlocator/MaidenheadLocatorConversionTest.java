package uk.m0nom.maidenheadlocator;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.jupiter.api.Test;
import uk.m0nom.coords.LocationSource;

import static org.assertj.core.api.Assertions.assertThat;


public class MaidenheadLocatorConversionTest {
    @Test
    public void testLocatorToLatLng6Char() {
        GlobalCoordinates ll = MaidenheadLocatorConversion.locatorToCoords(LocationSource.UNDEFINED, "IO84ni");
        assertThat(String.format("%.3f", ll.getLongitude())).isEqualTo("-2.875");
        assertThat(String.format("%.3f", ll.getLatitude())).isEqualTo("54.354");
    }

    @Test
    public void testLocatorToLatLng10Char() {
        GlobalCoordinates ll = MaidenheadLocatorConversion.locatorToCoords(LocationSource.UNDEFINED, "IO84mj91mb");
        assertThat(String.format("%.3f", ll.getLongitude())).isEqualTo("-2.921");
        assertThat(String.format("%.3f", ll.getLatitude())).isEqualTo("54.379");
    }
}
