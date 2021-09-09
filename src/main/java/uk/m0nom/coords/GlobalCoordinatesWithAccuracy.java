package uk.m0nom.coords;

import lombok.Getter;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;

@Getter
@Setter
public class GlobalCoordinatesWithAccuracy extends GlobalCoordinates {
    private double latitudeAccuracy;
    private double longitudeAccuracy;

    public GlobalCoordinatesWithAccuracy(double latitude, double longitude) {
        super(latitude, longitude);
        latitudeAccuracy = 1;
        longitudeAccuracy = 1;
    }

    public GlobalCoordinatesWithAccuracy(GlobalCoordinates coordinates, double latitudeAccuracy, double longitudeAccuracy) {
        super(coordinates.getLatitude(), coordinates.getLongitude());
        setLatitudeAccuracy(latitudeAccuracy);
        setLongitudeAccuracy(longitudeAccuracy);
    }
    public GlobalCoordinatesWithAccuracy(double latitude, double longitude, double latitudeAccuracy) {
        super(latitude, longitude);
        setLatitudeAccuracy(latitudeAccuracy);
        setLongitudeAccuracy(longitudeAccuracy);
    }
}
