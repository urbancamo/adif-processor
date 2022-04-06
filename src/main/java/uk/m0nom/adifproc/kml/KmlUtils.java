package uk.m0nom.adifproc.kml;

/**
 * Utility classes supporting KML rendering for Google Earth
 */
public class KmlUtils {

    public static String getStyleId(String id) {
        return String.format("style_%s", id);
    }

    public static String getStyleUrl(String id) {
        return String.format("#%s", getStyleId(id));
    }

    public static String getModeStyleId(String id) {
        return String.format("style_%s_mode", id);
    }

    public static String getModeStyleUrl(String id) {
        return String.format("#%s_mode", getStyleId(id));
    }

    public static String getModeId(String id) {
        return String.format("%s_mode", id);
    }
}
