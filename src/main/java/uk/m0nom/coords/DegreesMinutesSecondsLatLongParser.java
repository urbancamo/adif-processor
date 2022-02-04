package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DegreesMinutesSecondsLatLongParser implements LocationParser, LocationFormatter {
    private final static String DMS_PATTERN = "\\s*([-+]*)(\\d+)\\s*\"*\\s*(\\d+)(\\d+)\\s*'*\\s*(\\d+\\.\\d+)(\\d+)\\s*\"*\\s*";
    private final static Pattern PATTERN = Pattern.compile("^" + DMS_PATTERN + DMS_PATTERN + "$");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoords3D parse(LocationSource source, String location) {
        Matcher matcher = getPattern().matcher(location);
        if (matcher.find()) {
            String latNegative = matcher.group(1);
            String latDegrees = matcher.group(2);
            String latMinutes = matcher.group(3);
            String latSeconds = matcher.group(4);

            String longNegative = matcher.group(5);
            String longDegrees = matcher.group(6);
            String longMinutes = matcher.group(7);
            String longSeconds = matcher.group(8);

            Double latitude = LatLongUtils.parseDegreesMinutesSeconds(latDegrees, latMinutes, latSeconds, "-".equalsIgnoreCase(latNegative));
            Double longitude = LatLongUtils.parseDegreesMinutesSeconds(longDegrees, longMinutes, longSeconds, "-".equalsIgnoreCase(longNegative));
            return new GlobalCoords3D(latitude, longitude, source, LocationAccuracy.LAT_LONG);
        }
        return null;
    }

    @Override
    public String format(GlobalCoordinates coords) {
        return String.format("%.0f° %.0f' %d\", %.0f° %.0f' %d\"",
                LatLongUtils.getDegreesLat(coords),
                Math.abs(LatLongUtils.getWholeMinutesLat(coords)),
                Math.abs(Math.round(LatLongUtils.getSecondsLat(coords))),
                LatLongUtils.getDegreesLong(coords),
                Math.abs(LatLongUtils.getWholeMinutesLong(coords)),
                Math.abs(Math.round(LatLongUtils.getSecondsLong(coords))));
    }

    @Override
    public String getName() {
        return "Degrees Minutes Seconds Lat/Long";
    }
}
