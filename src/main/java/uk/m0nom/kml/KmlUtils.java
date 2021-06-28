package uk.m0nom.kml;

/**
 * Utility classes supporting KML rendering for Google Earth
 */
public class KmlUtils {
    public final static String kmlColour(String transparency, String red, String green, String blue) {
        return String.format("%s%s%s%s", transparency, red, green, blue);
    }
}
