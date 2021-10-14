package uk.m0nom.geocoding;

import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.model.Address;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.coords.GlobalCoordinatesWithLocationSource;
import uk.m0nom.coords.LocationSource;
import uk.m0nom.qrz.QrzCallsign;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Use the Open Streetmap Nominatim API to try and determine a station's location based on a full or
 * partial match of their QRZ.com address details.
 */
public class NominatimGeocodingProvider implements GeocodingProvider {
    private final static long DELAY = 1000L;

    private static final Logger logger = Logger.getLogger(NominatimGeocodingProvider.class.getName());
    private long lastTimestamp;

    /** Cache is static and lasts for the lifetime of the application on the server */
    private final static GeocodingCache cache = new GeocodingCache();

    public NominatimGeocodingProvider() {
        lastTimestamp = new Date().getTime();
    }

    @Override
    public GlobalCoordinatesWithLocationSource getLocationFromAddress(QrzCallsign qrzData) throws IOException, InterruptedException {
        GlobalCoordinatesWithLocationSource coords = null;

        String addressToCheck = addressStringFromQrzData(qrzData);
        if (addressToCheck != null) {
            coords = cache.get(addressToCheck);
            if (coords == null) {
                coords = queryUsingAddressSubstring(qrzData.getCall(), addressToCheck);
                if (coords != null) {
                    // Stick in the cache with the original search string
                    cache.put(addressToCheck, coords);
                }
            } else {
                logger.info(String.format("Geocoding cache hit for address: %s", addressToCheck));
            }
        }
        return coords;
    }

    private String addressStringFromQrzData(QrzCallsign qrzData) {
        String addressToCheck = null;
        String searchString = "";
        searchString = addIfNotNull(searchString, qrzData.getAddr1());
        searchString = addIfNotNull(searchString, qrzData.getAddr2());
        searchString = addIfNotNull(searchString, qrzData.getCounty());
        searchString = addIfNotNull(searchString, qrzData.getCountry());
        if (searchString.length() > 2) {
            searchString = searchString.substring(2);
            addressToCheck = searchString.replace(",,", ",");
        }
        return addressToCheck;
    }

    private GlobalCoordinatesWithLocationSource queryUsingAddressSubstring(String callsign, String addressToCheck) throws IOException, InterruptedException {
        String substring = addressToCheck;
        GlobalCoordinatesWithLocationSource coords = null;
        int accuracy = 0;
        while (StringUtils.isNotBlank(substring) && coords == null) {
            // Start cutting down the address, with the most specific information first
            coords = addressSearch(callsign, substring, accuracy++);
            substring = StringUtils.trim(substring.substring(substring.indexOf(',')+1));
        }
        return coords;
    }

    private GlobalCoordinatesWithLocationSource addressSearch(String callsign, String searchString, int accuracy) throws IOException, InterruptedException {
        long timeDiff = new Date().getTime() - lastTimestamp;
        if (timeDiff < DELAY) {
            long pause = DELAY - timeDiff;
            // Ensure at least a second between calls to comply with fair usage policy
            logger.info(String.format("Pausing for a %d ms to comply with NominatimGeocodingProvider fair usage policy", pause));
            Thread.sleep(pause);
        }
        logger.info(String.format("Searching for a location for %s based on address search string: %s", callsign, searchString));
        List<Address> addressMatches = new JsonNominatimClient(HttpClientBuilder.create().build(), "mark@wickensonline.co.uk").search(StringUtils.trim(searchString));
        lastTimestamp = new Date().getTime();

        if (addressMatches.size() > 0) {
            Address match = addressMatches.get(0);
            LocationSource source = getLocationSourceFromAccuracy(accuracy);
            return new GlobalCoordinatesWithLocationSource(match.getLatitude(), match.getLongitude(), source);
        }
        return null;
    }

    private LocationSource getLocationSourceFromAccuracy(int accuracy) {
        if (accuracy == 0) return LocationSource.GEOLOCATION_VERY_GOOD;
        if (accuracy == 1) return LocationSource.GEOLOCATION_GOOD;
        if (accuracy == 2) return LocationSource.GEOLOCATION_POOR;
        return LocationSource.GEOLOCATION_VERY_POOR;
    }

    private String addIfNotNull(String current, String toAdd) {
        if (StringUtils.isNotEmpty(toAdd)) {
            return StringUtils.trim(current + ", " + toAdd);
        }
        return current;
    }
}
