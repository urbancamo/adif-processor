package uk.m0nom.adifproc.geocoding;

import org.junit.jupiter.api.Test;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.qrz.QrzCallsign;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


public class NominatimGeocodingProviderTest {
    @Test
    public void test2E0XUP() throws IOException, InterruptedException {
        QrzCallsign qrzData = new QrzCallsign();

        qrzData.setAddr1("Shinwell House, Central Road");
        qrzData.setAddr2("Dearham CA15 7HD");
        qrzData.setCountry("England");

        NominatimGeocodingProvider provider = new NominatimGeocodingProvider();
        GeocodingResult result = provider.getLocationFromAddress(qrzData);
        GlobalCoords3D coords = result.getCoordinates();

        // From Google Maps: 54.70503157515261, -3.451084602863399
        assertThat(Math.abs(coords.getLatitude() - 54.705)).isLessThan(0.1);
        assertThat(Math.abs(coords.getLongitude() + 3.451)).isLessThan(0.1);
    }
}
