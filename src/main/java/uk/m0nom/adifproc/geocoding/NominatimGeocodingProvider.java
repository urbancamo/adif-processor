package uk.m0nom.adifproc.geocoding;

import fr.dudie.nominatim.client.JsonNominatimClient;
import fr.dudie.nominatim.model.Address;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.coords.LocationAccuracy;
import uk.m0nom.adifproc.coords.LocationSource;
import uk.m0nom.adifproc.qrz.QrzCallsign;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

/**
 * Use the Open Streetmap Nominatim API to try and determine a station's location based on a full or
 * partial match of their QRZ.com address details.
 */
@Service
public class NominatimGeocodingProvider implements GeocodingProvider {
    private final static long DELAY = 1000L;

    private static final Logger logger = Logger.getLogger(NominatimGeocodingProvider.class.getName());
    private long lastTimestamp;

    /** Cache is static and lasts for the lifetime of the application on the server */
    private final static GeocodingCache cache = new GeocodingCache();

    public NominatimGeocodingProvider() {
        lastTimestamp = java.time.Instant.now().toEpochMilli();
    }

    @Override
    public GeocodingResult getLocationFromAddress(QrzCallsign qrzData) throws IOException, InterruptedException {
        GeocodingResult result = null;

        String addressToCheck = addressStringFromQrzData(qrzData);
        if (addressToCheck != null) {
            result = cache.get(addressToCheck);
            if (result == null) {
                result = queryUsingAddressSubstring(qrzData.getCall(), addressToCheck);
                // Stick in the cache with the original search string
                cache.put(addressToCheck, result);
            } else {
                logger.info(String.format("Geocoding cache hit for address: %s", addressToCheck));
            }
        }
        return result;
    }

    @Override
    public GeocodingResult getLocationFromAddress(String address) throws IOException, InterruptedException {
        return queryUsingAddressSubstring("CoordinateConverterController", address);
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

    private GeocodingResult queryUsingAddressSubstring(String callsign, String addressToCheck) throws IOException, InterruptedException {
        String substring = addressToCheck;
        GlobalCoords3D coords = null;
        int accuracy = 0;
        while (StringUtils.isNotBlank(substring) && coords == null) {
            // Start cutting down the address, with the most specific information first
            coords = addressSearch(callsign, substring, accuracy++);
            if (coords == null) {
                String newSubstring = StringUtils.trim(substring.substring(substring.indexOf(',') + 1));
                // Make sure we don't get caught in an endless loop.
                if (StringUtils.equals(newSubstring, substring)) {
                    return new GeocodingResult(null, substring, "No location found");
                } else {
                    substring = newSubstring;
                }
            }
        }
        return new GeocodingResult(coords, substring, null);
    }

    private GlobalCoords3D addressSearch(String callsign, String searchString, int accuracy) throws IOException, InterruptedException {
        long timeDiff = new Date().getTime() - lastTimestamp;
        if (timeDiff < DELAY) {
            long pause = DELAY - timeDiff;
            // Ensure at least a second between calls to comply with fair usage policy
            //logger.info(String.format("Pausing for a %d ms to comply with NominatimGeocodingProvider fair usage policy", pause));
            Thread.sleep(pause);
        }
        logger.info(String.format("Searching for a location for %s based on address search string: %s", callsign, searchString));
        List<Address> addressMatches = new JsonNominatimClient(HttpClientBuilder.create().build(), "mark@wickensonline.co.uk").search(StringUtils.trim(searchString));
        lastTimestamp = java.time.Instant.now().toEpochMilli();

        if (!addressMatches.isEmpty()) {
            Address match = addressMatches.get(0);
            LocationAccuracy locationAccuracy = getLocationAccuracy(accuracy);
            return new GlobalCoords3D(match.getLatitude(), match.getLongitude(), LocationSource.GEOCODING, locationAccuracy);
        }
        return null;
    }

    private LocationAccuracy getLocationAccuracy(int accuracy) {
        if (accuracy == 0) return LocationAccuracy.GEOLOCATION_VERY_GOOD;
        if (accuracy == 1) return LocationAccuracy.GEOLOCATION_GOOD;
        if (accuracy == 2) return LocationAccuracy.GEOLOCATION_POOR;
        return LocationAccuracy.GEOLOCATION_VERY_POOR;
    }

    private String addIfNotNull(String current, String toAdd) {
        if (StringUtils.isNotEmpty(toAdd)) {
            return StringUtils.trim(current + ", " + toAdd);
        }
        return current;
    }
}
