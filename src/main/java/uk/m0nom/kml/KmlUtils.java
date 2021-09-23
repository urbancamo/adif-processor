package uk.m0nom.kml;

/**
 * Utility classes supporting KML rendering for Google Earth
 */
public class KmlUtils {
    public static String kmlColour(String transparency, String red, String green, String blue) {
        return String.format("%s%s%s%s", transparency, red, green, blue);
    }

    public static String getStyleId(String id) {
        return String.format("style_%s", id);
    }

    public static String getStyleUrl(String id) {
        return String.format("#%s", getStyleId(id));
    }


}
