package uk.m0nom.geocoding;

import uk.m0nom.coords.GlobalCoordinatesWithLocationSource;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class GeocodingCache {
    private final static int MAXSIZE = 1000;

    private HashMap<String, GlobalCoordinatesWithLocationSource> cache = new LinkedHashMap<>() {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, GlobalCoordinatesWithLocationSource> eldest) {
            return this.size() > MAXSIZE;
        }
    };

    public GlobalCoordinatesWithLocationSource get(String address) {
        return cache.get(address);
    }

    public void put(String address, GlobalCoordinatesWithLocationSource coords) {
        cache.put(address, coords);
    }
}
