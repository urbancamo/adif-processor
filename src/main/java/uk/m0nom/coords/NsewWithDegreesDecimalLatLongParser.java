package uk.m0nom.coords;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NsewWithDegreesDecimalLatLongParser implements LocationParser {
    private final static Pattern PATTERN = Pattern.compile("([NnSs])(\\d+\\.\\d+)\\s+([EeWwOo])(\\d+\\.\\d+)");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoordinatesWithSourceAccuracy parse(LocationSource source, String location) {
        Matcher matcher = getPattern().matcher(location);

        if (matcher.find()) {
            String latNorthSouth = matcher.group(1).toUpperCase();
            String latDegrees = matcher.group(2);

            String longEastWest = matcher.group(3).toUpperCase();
            String longDegrees = matcher.group(4);

            Double latitude = LatLongUtils.parseDegDecimalMinLatitude(latDegrees, "0", latNorthSouth);
            Double longitude = LatLongUtils.parseDegDecimalMinLongitude(longDegrees, "0", longEastWest);
            if (latitude == null || longitude == null) {
                throw new UnsupportedOperationException();
            }
            return new GlobalCoordinatesWithSourceAccuracy(latitude, longitude, source, LocationAccuracy.LAT_LONG);
        }
        return null;
    }
}
