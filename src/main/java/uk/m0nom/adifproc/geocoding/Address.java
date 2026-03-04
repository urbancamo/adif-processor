package uk.m0nom.adifproc.geocoding;

/**
 * Represents a geocoded address result from the Nominatim API.
 */
public record Address(double latitude, double longitude, String displayName) {

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getDisplayName() {
        return displayName;
    }
}
