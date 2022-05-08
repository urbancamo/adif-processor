package org.marsik.ham.grid;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CoordinateWriter {
    private static final Pattern LAT_RE = Pattern.compile("([NS]) ?([0-9]{0,3}) +([0-9]{1,2}(\\.[0-9]+)?)", Pattern.CASE_INSENSITIVE);
    private static final Pattern LON_RE = Pattern.compile("([EW]) ?([01]?[0-9]{0,3}) +([0-9]{1,2}(\\.[0-9]+)?)", Pattern.CASE_INSENSITIVE);

    private static String getLatitudePrefix(Double lat) {
        return lat >= 0.0 ? "N" : "S";
    }

    private static String getLongitudePrefix(Double lat) {
        return lat >= 0.0 ? "E" : "W";
    }

    public static String lonToDM(double lon) {
        String prefix = getLongitudePrefix(lon);
        return coordToDM(prefix, lon);
    }

    public static String latToDM(double lat) {
        String prefix = getLatitudePrefix(lat);
        return coordToDM(prefix, lat);
    }

    private static String coordToDM(String prefix, double coord) {
        double absCoord = Math.abs(coord);
        int deg = (int) Math.floor(absCoord);
        double min = (absCoord - Math.floor(absCoord)) * 60.0;
        int wholeMin = (int)Math.floor(min);
        int remainder = (int)((min - wholeMin) * 1000);

        return String.format("%s%03d %02d.%03d", prefix, deg, wholeMin, remainder);
    }

    public static double dmToLat(String string) {
        Matcher matcher = LAT_RE.matcher(string);
        if (matcher.matches()) {
            String prefix = matcher.group(1);
            int deg = Integer.parseInt(matcher.group(2));
            double min = Double.parseDouble(matcher.group(3));
            return (prefix.equalsIgnoreCase("N") ? 1 : -1) * (deg + (min / 60.0));
        } else {
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("Bad latitude format: '%s'", string));
            }
        }
    }

    public static double dmToLon(String string) {
        Matcher matcher = LON_RE.matcher(string);
        if (matcher.matches()) {
            String prefix = matcher.group(1);
            int deg = Integer.parseInt(matcher.group(2));
            double min = Double.parseDouble(matcher.group(3));
            return (prefix.equalsIgnoreCase("E") ? 1 : -1) * (deg + (min / 60.0));
        } else {
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException(String.format("Bad longitude format: '%s'", string));
            }
        }
    }
}
