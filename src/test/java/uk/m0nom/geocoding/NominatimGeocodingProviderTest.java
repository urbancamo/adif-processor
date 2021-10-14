package uk.m0nom.geocoding;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.junit.Ignore;
import org.junit.Test;
import uk.m0nom.qrz.QrzCallsign;

import java.io.IOException;

import static org.junit.Assert.assertTrue;

public class NominatimGeocodingProviderTest {
    @Ignore
    @Test
    public void test2E0XUP() throws IOException, InterruptedException {
        QrzCallsign qrzData = new QrzCallsign();

        qrzData.setAddr1("Shinwell House, Central Road");
        qrzData.setAddr2("Dearham CA15 7HD");
        qrzData.setCountry("England");

        NominatimGeocodingProvider provider = new NominatimGeocodingProvider();
        GlobalCoordinates coords = provider.getLocationFromAddress(qrzData);

        // From Google Maps: 54.70503157515261, -3.451084602863399
        assertTrue(Math.abs(coords.getLatitude() - 54.705) < 0.1);
        assertTrue(Math.abs(coords.getLongitude() + 3.451) < 0.1);
    }
}
