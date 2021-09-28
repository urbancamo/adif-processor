package uk.m0nom.geocoding;

import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.model.Address;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.qrz.QrzCallsign;

import java.io.IOException;
import java.util.List;
import java.util.logging.Logger;

/**
 * Use the Open Streetmap Nominatim API to try and determine a station's location based on a full or
 * partial match of their QRZ.com address details.
 */
public class NominatimGeocodingProvider implements GeocodingProvider {
    private static final Logger logger = Logger.getLogger(NominatimGeocodingProvider.class.getName());

    public NominatimGeocodingProvider() {
    }

    @Override
    public GlobalCoordinates getLocationFromAddress(QrzCallsign qrzData) throws IOException, InterruptedException {
        String searchString = "";
        String substring;
        searchString = addIfNotNull(searchString, qrzData.getAddr1());
        searchString = addIfNotNull(searchString, qrzData.getAddr2());
        searchString = addIfNotNull(searchString, qrzData.getCounty());
        searchString = addIfNotNull(searchString, qrzData.getCountry());
        if (searchString.length() > 2) {
            searchString = searchString.substring(2);
            substring = searchString.replace(",,", ",");
        } else {
            return null;
        }

        GlobalCoordinates coords = null;

        while (StringUtils.isNotBlank(substring) && coords == null) {
            // Start cutting down the address, with the most specific information first
            coords = addressSearch(qrzData.getCall(), substring);
            substring = StringUtils.trim(substring.substring(substring.indexOf(',')+1));
        }
        return coords;
    }

    private GlobalCoordinates addressSearch(String callsign, String searchString) throws IOException, InterruptedException {
        logger.info(String.format("Searching for a location for %s based on address search string: %s", callsign, searchString));
        List<Address> addressMatches = new JsonNominatimClient(HttpClientBuilder.create().build(), "mark@wickensonline.co.uk").search(StringUtils.trim(searchString));
        // Pause for a second to comply with fair usage policy
        logger.info("Pausing for a second to comply with NominatimGeocodingProvider fair usage policy");
        Thread.sleep(1000);
        if (addressMatches.size() > 0) {
            Address match = addressMatches.get(0);
            return new GlobalCoordinates(match.getLatitude(), match.getLongitude());
        }
        return null;
    }

    private String addIfNotNull(String current, String toAdd) {
        if (StringUtils.isNotEmpty(toAdd)) {
            return StringUtils.trim(current + ", " + toAdd);
        }
        return current;
    }
}
