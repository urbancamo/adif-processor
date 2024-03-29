package uk.m0nom.adifproc.geocoding;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GeocodingCache {
    private final static int MAXSIZE = 1000;

    private final HashMap<String, GeocodingResult> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, GeocodingResult> eldest) {
            return this.size() > MAXSIZE;
        }
    };

    public GeocodingResult get(String address) {
        return cache.get(address);
    }

    public void put(String address, GeocodingResult result) {
        cache.put(address, result);
    }
}
