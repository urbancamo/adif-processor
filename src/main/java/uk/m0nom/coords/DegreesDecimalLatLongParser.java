package uk.m0nom.coords;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DegreesDecimalLatLongParser implements LocationParser {
    private final static Pattern PATTERN = Pattern.compile("([+-]*)(\\d+\\.\\d+)\\s+([+-]*)(\\d+\\.\\d+)");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoordinatesWithSourceAccuracy parse(LocationSource source, String location) {
        Matcher matcher = getPattern().matcher(location);

        String latNorthSouth = "N";
        String longEastWest = "E";

        if (matcher.find()) {
            String latSign = matcher.group(1);
            String latDegrees = matcher.group(2);
            if ("-".equals(latSign)) {
                latNorthSouth = "S";
            }
            String longSign = matcher.group(3);
            String longDegrees = matcher.group(4);
            if ("-".equals(longSign)) {
                longEastWest = "W";
            }
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
