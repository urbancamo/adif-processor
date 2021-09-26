package uk.m0nom.geocoding;

import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.client.NominatimClient;
import fr.dudie.nominatim.model.Address;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.qrz.QrzCallsign;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

public class NominatimGeocodingProvider implements GeocodingProvider {
    private static final Logger logger = Logger.getLogger(NominatimGeocodingProvider.class.getName());

    //private NominatimClient nominatimClient;

    public NominatimGeocodingProvider() {
        //nominatimClient = new JsonNominatimClient(HttpClientBuilder.create().build(), "");
    }

    @Override
    public GlobalCoordinates getLocationFromAddress(QrzCallsign qrzData) throws IOException, InterruptedException {
        String searchString = String.format("%s, %s, %s, %s", qrzData.getAddr1(), qrzData.getAddr2(), qrzData.getCounty(), qrzData.getCountry());
        if (qrzData.getCounty() == null) {
            searchString = String.format("%s, %s, %s", qrzData.getAddr1(), qrzData.getAddr2(), qrzData.getCountry());
        }
        GlobalCoordinates coords = addressSearch(qrzData.getCall(), searchString);
        if (coords != null) {
            return coords;
        }
        String substring = StringUtils.trim(searchString.substring(searchString.indexOf(',')+1));

        while (StringUtils.isNotBlank(substring) && coords == null) {
            // Start cutting down the address, with the most specific information first
            coords = addressSearch(qrzData.getCall(), substring);
            substring = StringUtils.trim(substring.substring(substring.indexOf(',')+1));
        }
        return coords;
    }

    private GlobalCoordinates addressSearch(String callsign, String searchString) throws IOException, InterruptedException {
        // Pause for a second to comply with fair usage policy
        logger.info("Pausing for a second to comply with NominatimGeocodingProvider fair usage policy");
        Thread.sleep(1000);
        logger.info(String.format("Searching for a location for %s based on address search string: %s", callsign, searchString));
        List<Address> addressMatches = new JsonNominatimClient(HttpClientBuilder.create().build(), "mark@wickensonline.co.uk").search(StringUtils.trim(searchString));
        if (addressMatches.size() > 0) {
            Address match = addressMatches.get(0);
            return new GlobalCoordinates(match.getLatitude(), match.getLongitude());
        }
        return null;
    }
}
