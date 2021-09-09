package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DegreesDecimalMinutesLatLongParser implements LocationParser {
    private final static Pattern PATTERN = Pattern.compile("(\\d+)[^\\d]+(\\d+\\.\\d+).*([NnSs])[^\\d]*(\\d+)[^\\d]+(\\d+\\.\\d+).*([EeWw])");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoordinatesWithAccuracy parse(String latLongString) {
        Matcher matcher = getPattern().matcher(latLongString);
        if (matcher.find()) {
            String latDegrees = matcher.group(1);
            String latMinutes = matcher.group(2);
            String latNorthSouth = matcher.group(3).toUpperCase();

            String longDegrees = matcher.group(4);
            String longMinutes = matcher.group(5);
            String longEastWest = matcher.group(6).toUpperCase();

            Double latitude = LatLongUtils.parseDegDecimalMinLatitude(latDegrees, latMinutes, latNorthSouth);
            Double longitude = LatLongUtils.parseDegDecimalMinLongitude(longDegrees, longMinutes, longEastWest);
            if (latitude == null || longitude == null) {
                throw new UnsupportedOperationException();
            }
            return new GlobalCoordinatesWithAccuracy(latitude, longitude);
        }
        return null;
    }
}
