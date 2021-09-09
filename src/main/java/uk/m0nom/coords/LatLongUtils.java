package uk.m0nom.coords;

public class LatLongUtils {

    public static Double parseDecimalLatitude(String latString) {
        try {
            Double latitude = Double.parseDouble(latString);
            // Check in range
            if (checkLatitudeRange(latitude)) {
                return latitude;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    public static Double parseDecimalLatitude(String latString, String latNorthSouth) {
        try {
            Double latitude = Double.parseDouble(latString);
            if ("S".equalsIgnoreCase(latNorthSouth)) {
                return -latitude;
            }
            // Check in range
            if (checkLatitudeRange(latitude)) {
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
            if (checkLongitudeRange(longitude)) {
                return longitude;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    public static Double parseDecimalLongitude(String longString, String longEastWest) {
        try {
            Double longitude = Double.parseDouble(longString);
            if ("W".equalsIgnoreCase(longEastWest)) {
                longitude = -longitude;
            }
            // Check in range
            if (checkLongitudeRange(longitude)) {
                return longitude;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    public static Double parseDecimalLatitudeWithNs(String latString, String latNorthSouth) {
        try {
            Double latitude = Double.parseDouble(latString);
            if ("S".equalsIgnoreCase(latNorthSouth)) {
                latitude = -latitude;
            }
            // Check in range
            if (checkLatitudeRange(latitude)) {
                return latitude;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    public static Double parseDecimalLongitudeWithEw(String longString, String longEastWest) {
        try {
            Double longitude = Double.parseDouble(longString);
            if ("W".equalsIgnoreCase(longEastWest)) {
                longitude = -longitude;
            }
            // Check in range
            if (checkLongitudeRange(longitude)) {
                return longitude;
            }
        } catch (NumberFormatException e) {
            return null;
        }
        return null;
    }

    public static Double parseDegMinSecLatitude(String latDegrees, String latMinutes, String latSeconds, String latNorthSouth) {
        Double decimal = parseDegreesMinutesSeconds(latDegrees, latMinutes, latSeconds, "S".equalsIgnoreCase(latNorthSouth));
        if (decimal != null && checkLatitudeRange(decimal)) {
            return decimal;
        }
        return null;
    }

    public static Double parseDegMinSecLongitude(String longDegrees, String longMinutes, String longSeconds, String longEastWest) {
        Double decimal = parseDegreesMinutesSeconds(longDegrees, longMinutes, longSeconds, "W".equalsIgnoreCase(longEastWest));
        if (decimal != null && checkLatitudeRange(decimal)) {
            return decimal;
        }
        return null;
    }

    private static Double parseDegreesMinutesSeconds(String degrees, String minutes, String seconds, boolean negative) {
        try {
            Double d = Double.parseDouble(degrees);
            if (negative) {
                d = -d;
            }
            Double m = Double.parseDouble(minutes);
            Double s = Double.parseDouble(seconds);
            return Math.signum(d) * (Math.abs(d) + (m / 60.0) + (s / 3600.0));
        } catch (NumberFormatException e) {
            return null;
        }
    }


    public static Double parseDegDecimalMinLatitude(String latDegrees, String latMinutes, String latNorthSouth) {
        Double decimal = parseDegreesMinutesSeconds(latDegrees, latMinutes, "0.0", "S".equalsIgnoreCase(latNorthSouth));
        if (decimal != null && checkLatitudeRange(decimal)) {
            return decimal;
        }
        return null;
    }

    public static Double parseDegDecimalMinLongitude(String longDegrees, String longMinutes, String longEastWest) {
        Double decimal = parseDegreesMinutesSeconds(longDegrees, longMinutes, "0.0", "W".equalsIgnoreCase(longEastWest));
        if (decimal != null && checkLatitudeRange(decimal)) {
            return decimal;
        }
        return null;
    }


    private static boolean checkLatitudeRange(double latitude) {
        return (latitude >= -90.0 || latitude <= 90.0) ;
    }

    private static boolean checkLongitudeRange(double longitude) {
        return (longitude >= -180.0 || longitude <= 180.0);
    }
}
