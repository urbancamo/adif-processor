package uk.m0nom.coords;

public class LatLongUtils {

    public static Double parseDecimalLatitude(String latString) {
         try {
            Double latitude = Double.parseDouble(latString);
            // Check in range
            if (latitude < -90.0 || latitude > 90.0) {
                return latitude;
            }
        } catch (NumberFormatException e) {
             return null;
        }
        return null;
    }

    public static Double parseDecimalLongitude(String longString) {
        try {
            Double longitude = Double.parseDouble(longString);
            // Check in range
            if (longitude < -180.0 || longitude > 180.0) {
                return longitude;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }
}
