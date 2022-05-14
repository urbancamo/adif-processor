package uk.m0nom.adifproc.coords;

import lombok.Getter;

/**
 * Indicates, for any location stored, the accuracy of the location based on the source information, where possible
 */
@Getter
public enum LocationAccuracy {
    MHL4("4 Character Maidenhead"),
    MHL6("6 Character Maidenhead"),
    MHL8("8 Character Maidenhead"),
    MHL10("10 Character Maidenhead"),
    LAT_LONG("Latitude Longitude"),
    WWFF("WWFF Reference"),
    OSGB36_3DIGIT("3 Digit UK Locator"),
    OSGB36_4DIGIT("4 Digit UK Locator"),
    OSGB36_5DIGIT("5 Digit UK Locator"),
    OSGB36_6DIGIT("6 Digit UK Locator"),
    GEOLOCATION_VERY_GOOD("Geolocation - Very Good"),
    GEOLOCATION_GOOD("Geolocation - Good"),
    GEOLOCATION_POOR("Geolocation - Poor"),
    GEOLOCATION_VERY_POOR("Geolocation - Very Poor"),
    IRISH_GRID_REF_5DIGIT("5 Digit Irish Locator");

    private final String description;

    LocationAccuracy(String description) {
        this.description = description;
    }
}
