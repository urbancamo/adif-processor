package uk.m0nom.adifproc.coords;

import lombok.Getter;

@Getter
public enum LocationSource {
    ACTIVITY("Activity"),
    OVERRIDE("Overriden"),
    QRZ("QRZ.COM"),
    GEOCODING("Geocoding"),
    OSGB36_CONVERTER("OSGB36 Converter"),
    UNDEFINED("Undefined"),
    SATELLITE("Satellite");

    private final String description;

    LocationSource(String description) {
        this.description = description;
    }
}