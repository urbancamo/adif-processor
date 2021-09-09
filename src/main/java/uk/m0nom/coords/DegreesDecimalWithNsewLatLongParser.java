package uk.m0nom.coords;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DegreesDecimalWithNsewLatLongParser implements LocationParser {
    private final static Pattern PATTERN = Pattern.compile("(\\d+\\.\\d+)[°]*([NnSs])\\s+(\\d+\\.\\d+)[°]*([EeWwOo])");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoordinatesWithLocationSource parse(String location) {
        Matcher matcher = getPattern().matcher(location);

        if (matcher.find()) {
            String latDegrees = matcher.group(1);
            String latNorthSouth = matcher.group(2).toUpperCase();

            String longDegrees = matcher.group(3);
            String longEastWest = matcher.group(4).toUpperCase();

            Double latitude = LatLongUtils.parseDegDecimalMinLatitude(latDegrees, "0", latNorthSouth);
            Double longitude = LatLongUtils.parseDegDecimalMinLongitude(longDegrees, "0", longEastWest);
            if (latitude == null || longitude == null) {
                throw new UnsupportedOperationException();
            }
            return new GlobalCoordinatesWithLocationSource(latitude, longitude);
        }
        return null;
    }
}
