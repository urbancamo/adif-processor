package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DegreesDecimalMinutesLatLongParser implements LocationParser, LocationFormatter {
    private final static Pattern PATTERN = Pattern.compile("([-+]*)(\\d+)[^\\d]+(\\d+\\.\\d+)'*\\s+([-+]*)(\\d+)[^\\d]+(\\d+\\.\\d+)'*");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoordinatesWithSourceAccuracy parse(LocationSource source, String location) {
        Matcher matcher = getPattern().matcher(location);
        if (matcher.find()) {
            String latNegative = matcher.group(1);
            String latDegrees = matcher.group(2);
            String latMinutes = matcher.group(3);

            String longNegative = matcher.group(4);
            String longDegrees = matcher.group(5);
            String longMinutes = matcher.group(6);

            Double latitude = LatLongUtils.parseDegreesMinutes(latDegrees, latMinutes, "-".equalsIgnoreCase(latNegative));
            Double longitude = LatLongUtils.parseDegreesMinutes(longDegrees, longMinutes, "-".equalsIgnoreCase(longNegative));
            return new GlobalCoordinatesWithSourceAccuracy(latitude, longitude, source, LocationAccuracy.LAT_LONG);
        }
        return null;
    }

    @Override
    public String format(GlobalCoordinates coords) {
        return String.format("%.0f° %.3f', %.0f° %.3f'",
                LatLongUtils.getDegreesLat(coords),
                Math.abs(LatLongUtils.getMinutesLat(coords)),
                LatLongUtils.getDegreesLong(coords),
                Math.abs(LatLongUtils.getMinutesLong(coords)));
    }

    @Override
    public String getName() {
        return "Degrees Decimal Minutes Lat/Long";
    }
}
