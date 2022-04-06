package uk.m0nom.adifproc.geocoding;

import uk.m0nom.adifproc.qrz.QrzCallsign;

import java.io.IOException;

public interface GeocodingProvider {
    GeocodingResult getLocationFromAddress(QrzCallsign qrzData) throws IOException, InterruptedException;
    GeocodingResult getLocationFromAddress(String address) throws IOException, InterruptedException;
}
