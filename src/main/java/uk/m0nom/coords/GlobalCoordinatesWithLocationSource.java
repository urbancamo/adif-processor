package uk.m0nom.coords;

import lombok.Getter;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;

@Getter
@Setter
public class GlobalCoordinatesWithLocationSource extends GlobalCoordinates {
    private LocationSource source;
    private Double altitude;

    public GlobalCoordinatesWithLocationSource(double latitude, double longitude) {
        super(latitude, longitude);
        setSource(LocationSource.LAT_LONG);
    }

    public GlobalCoordinatesWithLocationSource(double latitude, double longitude, LocationSource source) {
        super(latitude, longitude);
        setSource(source);
    }

    public GlobalCoordinatesWithLocationSource(GlobalCoordinates coordinates, LocationSource source) {
        super(coordinates.getLatitude(), coordinates.getLongitude());
        setSource(source);
    }

    public GlobalCoordinatesWithLocationSource(double latitude, double longitude, Double altitude) {
        super(latitude, longitude);
        setAltitude(altitude);
        setSource(LocationSource.LAT_LONG);
    }

    public GlobalCoordinatesWithLocationSource(double latitude, double longitude, Double altitude, LocationSource source) {
        super(latitude, longitude);
        setAltitude(altitude);
        setSource(source);
    }

    public GlobalCoordinatesWithLocationSource(GlobalCoordinates coordinates, Double altitude, LocationSource source) {
        super(coordinates.getLatitude(), coordinates.getLongitude());
        setAltitude(altitude);
        setSource(source);
    }

    public boolean hasAltitude() { return altitude != null; }
}
