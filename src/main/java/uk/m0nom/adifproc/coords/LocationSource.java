package uk.m0nom.adifproc.coords;

import lombok.Getter;

@Getter
public enum LocationSource {
    FROM_ADIF("From ADIF"),
    ACTIVITY("Activity"),
    OVERRIDE("Overriden"),
    QRZ("QRZ.COM"),
    GEOCODING("Geocoding"),
    OSGB36_CONVERTER("OSGB36 Grid Reference"),
    WAB("Worked All Britain Square"),
    IRISH_GRID_REF_CONVERTER("Irish Grid Reference"),
    UNDEFINED("Undefined"),
    SATELLITE("Satellite");

    private final String description;

    LocationSource(String description) {
        this.description = description;
    }
}