package uk.m0nom.geocoding;

import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GeocodingCache {
    private final static int MAXSIZE = 1000;

    private HashMap<String, GlobalCoordinatesWithSourceAccuracy> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, GlobalCoordinatesWithSourceAccuracy> eldest) {
            return this.size() > MAXSIZE;
        }
    };

    public GlobalCoordinatesWithSourceAccuracy get(String address) {
        return cache.get(address);
    }

    public void put(String address, GlobalCoordinatesWithSourceAccuracy coords) {
        cache.put(address, coords);
    }
}
