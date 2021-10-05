package uk.m0nom.geocoding;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GeocodingCache {
    private final static int MAXSIZE = 1000;

    private HashMap<String, GlobalCoordinates> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, GlobalCoordinates> eldest) {
            return this.size() > MAXSIZE;
        }
    };

    public GlobalCoordinates get(String address) {
        return cache.get(address);
    }

    public void put(String address, GlobalCoordinates coords) {
        cache.put(address, coords);
    }
}
