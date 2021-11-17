package uk.m0nom.coords;

import lombok.Getter;

@Getter
public enum LocationAccuracy {
    MHL4("Latitude/Longitude"),
    MHL6("6-CHAR Maidenhead"),
    MHL8("8-CHAR Maidenhead"),
    MHL10("10-CHAR Maidenhead"),
    LAT_LONG("Latitude Longitude"),
    WWFF("WWFF Reference"),
    OSGB36("UK Locator"),
    GEOLOCATION_VERY_GOOD("Geolocation - Very Good"),
    GEOLOCATION_GOOD("Geolocation - Good"),
    GEOLOCATION_POOR("Geolocation - Poor"),
    GEOLOCATION_VERY_POOR("Geolocation - Very Poor");

    private final String description;

    LocationAccuracy(String description) {
        this.description = description;
    }
}
