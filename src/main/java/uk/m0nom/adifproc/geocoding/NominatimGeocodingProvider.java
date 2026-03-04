package uk.m0nom.adifproc.geocoding;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.coords.LocationAccuracy;
import uk.m0nom.adifproc.coords.LocationSource;
import uk.m0nom.adifproc.qrz.QrzCallsign;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * Use the Open Streetmap Nominatim API to try and determine a station's location based on a full or
 * partial match of their QRZ.com address details.
 */
@Service
public class NominatimGeocodingProvider implements GeocodingProvider {
    private final static long DELAY = 1000L;

    private static final Logger logger = LoggerFactory.getLogger(NominatimGeocodingProvider.class);
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
        logger.debug("getLocationFromAddress called for callsign: {}, constructed address: {}", qrzData.getCall(), addressToCheck);
        if (addressToCheck != null) {
            result = cache.get(addressToCheck);
            if (result == null) {
                logger.debug("Cache miss for address: {}, querying Nominatim", addressToCheck);
                result = queryUsingAddressSubstring(qrzData.getCall(), addressToCheck);
                // Stick in the cache with the original search string
                cache.put(addressToCheck, result);
                logger.debug("Cached result for address: {}, result: {}", addressToCheck, result);
            } else {
                logger.info("Geocoding cache hit for address: {}", addressToCheck);
            }
        } else {
            logger.debug("No address could be constructed from QRZ data for callsign: {}", qrzData.getCall());
        }
        return result;
    }

    @Override
    public GeocodingResult getLocationFromAddress(String address) throws IOException, InterruptedException {
        logger.debug("getLocationFromAddress called with raw address: {}", address);
        return queryUsingAddressSubstring("CoordinateConverterController", address);
    }

    private String addressStringFromQrzData(QrzCallsign qrzData) {
        String addressToCheck = null;
        String searchString = "";
        searchString = addIfNotNull(searchString, qrzData.getAddr1());
        searchString = addIfNotNull(searchString, qrzData.getAddr2());
        searchString = addIfNotNull(searchString, qrzData.getCounty());
        searchString = addIfNotNull(searchString, qrzData.getCountry());
        logger.debug("QRZ address fields - addr1: {}, addr2: {}, county: {}, country: {}, combined: {}",
                qrzData.getAddr1(), qrzData.getAddr2(), qrzData.getCounty(), qrzData.getCountry(), searchString);
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
        logger.debug("queryUsingAddressSubstring started for callsign: {}, address: {}", callsign, addressToCheck);
        while (StringUtils.isNotBlank(substring) && coords == null) {
            // Start cutting down the address, with the most specific information first
            coords = addressSearch(callsign, substring, accuracy++);
            if (coords == null) {
                String newSubstring = StringUtils.trim(substring.substring(substring.indexOf(',') + 1));
                // Make sure we don't get caught in an endless loop.
                if (newSubstring != null && newSubstring.equals(substring)) {
                    logger.debug("No location found after exhausting address substrings, last tried: {}", substring);
                    return new GeocodingResult(null, substring, "No location found");
                } else {
                    logger.debug("No match for '{}', trimming to: '{}'", substring, newSubstring);
                    substring = newSubstring;
                }
            }
        }
        logger.debug("queryUsingAddressSubstring result for {}: coords={}, matchedAddress={}", callsign, coords, substring);
        return new GeocodingResult(coords, substring, null);
    }

    private GlobalCoords3D addressSearch(String callsign, String searchString, int accuracy) throws IOException, InterruptedException {
        long timeDiff = new Date().getTime() - lastTimestamp;
        if (timeDiff < DELAY) {
            long pause = DELAY - timeDiff;
            logger.debug("Pausing for {} ms to comply with Nominatim fair usage policy", pause);
            Thread.sleep(pause);
        }
        logger.info("Searching for a location for {} based on address search string: {}", callsign, searchString);
        List<Address> addressMatches = new JsonNominatimClient(HttpClientBuilder.create().build(), "mark@wickensonline.co.uk").search(StringUtils.trim(searchString));
        lastTimestamp = java.time.Instant.now().toEpochMilli();
        logger.debug("Nominatim returned {} address matches for search: {}", addressMatches.size(), searchString);

        if (!addressMatches.isEmpty()) {
            Address match = addressMatches.getFirst();
            LocationAccuracy locationAccuracy = getLocationAccuracy(accuracy);
            logger.debug("Best match: displayName={}, lat={}, lon={}, accuracy={}",
                    match.getDisplayName(), match.getLatitude(), match.getLongitude(), locationAccuracy);
            return new GlobalCoords3D(match.getLatitude(), match.getLongitude(), LocationSource.GEOCODING, locationAccuracy);
        }
        logger.debug("No address matches returned from Nominatim for: {}", searchString);
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
