package uk.m0nom.coords;

import lombok.Getter;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;

import static uk.m0nom.coords.LocationAccuracy.*;

@Getter
@Setter
public class GlobalCoordinatesWithSourceAccuracy extends GlobalCoordinates {
    private LocationInfo locationInfo;
    private Double altitude;

    public GlobalCoordinatesWithSourceAccuracy(double latitude, double longitude) {
        super(latitude, longitude);
        setLocationInfo(new LocationInfo(LAT_LONG));
    }

    public GlobalCoordinatesWithSourceAccuracy(GlobalCoordinates coordinates, LocationSource source, LocationAccuracy accuracy) {
        super(coordinates.getLatitude(), coordinates.getLongitude());
        setLocationInfo(source, accuracy);
    }

    public GlobalCoordinatesWithSourceAccuracy(double latitude, double longitude, Double altitude) {
        super(latitude, longitude);
        setAltitude(altitude);
        setLocationInfo(new LocationInfo(LAT_LONG));
    }

    public GlobalCoordinatesWithSourceAccuracy(double latitude, double longitude, Double altitude, LocationInfo locationInfo) {
        super(latitude, longitude);
        setAltitude(altitude);
        setLocationInfo(locationInfo);
    }

    public GlobalCoordinatesWithSourceAccuracy(double latitude, double longitude, LocationSource source, LocationAccuracy accuracy) {
        super(latitude, longitude);
        setLocationInfo(source, accuracy);
    }

    public GlobalCoordinatesWithSourceAccuracy(double latitude, double longitude, Double altitude, LocationSource source, LocationAccuracy accuracy) {
        super(latitude, longitude);
        setAltitude(altitude);
        setLocationInfo(source, accuracy);
    }

    public GlobalCoordinatesWithSourceAccuracy(GlobalCoordinates coordinates, Double altitude, LocationInfo locationInfo) {
        super(coordinates.getLatitude(), coordinates.getLongitude());
        setAltitude(altitude);
        setLocationInfo(locationInfo);
    }

    public void setLocationInfo(LocationSource source, LocationAccuracy accuracy) {
        setLocationInfo(new LocationInfo(accuracy, source));
    }
}
