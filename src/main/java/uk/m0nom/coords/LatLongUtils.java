package uk.m0nom.coords;

import lombok.SneakyThrows;
import org.gavaghan.geodesy.GlobalCoordinates;

public class LatLongUtils {

    public static Double parseDecimalLatitude(String latString) {
        try {
            double latitude = Double.parseDouble(latString);
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
            double latitude = Double.parseDouble(latString);
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
            double longitude = Double.parseDouble(longString);
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
            double longitude = Double.parseDouble(longString);
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
            double latitude = Double.parseDouble(latString);
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
            double longitude = Double.parseDouble(longString);
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

    public static Double parseDegreesMinutes(String degrees, String minutes, boolean negative) {
        return parseDegreesMinutesSeconds(degrees, minutes, "0.0", negative);
    }

        public static Double parseDegreesMinutesSeconds(String degrees, String minutes, String seconds, boolean negative) {
        try {
            double d = Double.parseDouble(degrees);
            if (negative) {
                d = -d;
            }
            double m = Double.parseDouble(minutes);
            double s = Double.parseDouble(seconds);
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

    public static boolean isCoordinateValid(GlobalCoordinates coords) {
        boolean valid = false;

        if (coords != null) {
            if (checkLatitudeRange(coords.getLatitude()) && checkLongitudeRange(coords.getLongitude())) {
                valid = coords.getLatitude() != 0.0 && coords.getLongitude() != 0.0;
            }
        }
        return valid;
    }

    private static boolean checkLatitudeRange(double latitude) {
        return (latitude >= -90.0 || latitude <= 90.0) ;
    }

    private static boolean checkLongitudeRange(double longitude) {
        return (longitude >= -180.0 || longitude <= 180.0);
    }

    public static String getNorthSouth(GlobalCoordinates coords) {
        String northSouth = "N";
        if (coords.getLatitude() < 0) {
            northSouth = "S";
        }
        return northSouth;
    }

    public static String getEastWest(GlobalCoordinates coords) {
        String eastWest = "E";
        if (coords.getLongitude() < 0) {
            eastWest = "W";
        }
        return eastWest;
    }

    public static double getDegreesLat(GlobalCoordinates coords) {
        return floorWithNegativeHandling(coords.getLatitude());
    }

    public static double getMinutesLat(GlobalCoordinates coords) {
        return (coords.getLatitude() - getDegreesLat(coords)) * 60.0;
    }

    public static double getSecondsLat(GlobalCoordinates coords) {
        return (getMinutesLat(coords) - floorWithNegativeHandling(getMinutesLat(coords))) * 60.0;
    }

    public static double getDegreesLong(GlobalCoordinates coords) {
        return floorWithNegativeHandling(coords.getLongitude());
    }

    public static double getMinutesLong(GlobalCoordinates coords) {
        return (coords.getLongitude() - getDegreesLong(coords)) * 60.0;
    }

    public static double getSecondsLong(GlobalCoordinates coords) {
        return (getMinutesLong(coords) - floorWithNegativeHandling(getMinutesLong(coords))) * 60.0;
    }

    public static double floorWithNegativeHandling(double val) {
        if (val < 0.0) {
            return Math.ceil(val);
        }
        return Math.floor(val);
    }

    public static double getWholeMinutesLat(GlobalCoordinates coords) {
        return LatLongUtils.floorWithNegativeHandling(LatLongUtils.getMinutesLat(coords));
    }

    public static double getWholeMinutesLong(GlobalCoordinates coords) {
        return LatLongUtils.floorWithNegativeHandling(LatLongUtils.getMinutesLong(coords));
    }
}


