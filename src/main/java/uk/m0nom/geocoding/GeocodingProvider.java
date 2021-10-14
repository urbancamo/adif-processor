package uk.m0nom.geocoding;

import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;
import uk.m0nom.qrz.QrzCallsign;

import java.io.IOException;

public interface GeocodingProvider {
    GlobalCoordinatesWithSourceAccuracy getLocationFromAddress(QrzCallsign qrzData) throws IOException, InterruptedException;
}
