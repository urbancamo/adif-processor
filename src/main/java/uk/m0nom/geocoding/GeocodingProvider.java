package uk.m0nom.geocoding;

import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.coords.GlobalCoordinatesWithLocationSource;
import uk.m0nom.qrz.QrzCallsign;

import java.io.IOException;

public interface GeocodingProvider {
    GlobalCoordinatesWithLocationSource getLocationFromAddress(QrzCallsign qrzData) throws IOException, InterruptedException;
}
