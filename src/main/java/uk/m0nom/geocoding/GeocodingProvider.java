package uk.m0nom.geocoding;

import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.qrz.QrzCallsign;

import java.io.IOException;

public interface GeocodingProvider {
    GlobalCoordinates getLocationFromAddress(QrzCallsign qrzData) throws IOException, InterruptedException;
}
