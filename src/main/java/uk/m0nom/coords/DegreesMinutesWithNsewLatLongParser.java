package uk.m0nom.coords;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DegreesMinutesWithNsewLatLongParser implements LocationParser {
    private final static Pattern PATTERN = Pattern.compile("(\\d+)[^\\d]+(\\d+)[^NnSs]*([NnSs])[^\\d]+(\\d+)[^\\d]+(\\d+)[^EeWwOo]*([EeWwOo])");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoordinatesWithLocationSource parse(String location) {
        Matcher matcher = getPattern().matcher(location);

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
            return new GlobalCoordinatesWithLocationSource(latitude, longitude);
        }
        return null;
    }
}
