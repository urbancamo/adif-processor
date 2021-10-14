package uk.m0nom.coords;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DegreesMinutesSecondsLatLongParser  implements LocationParser {
    private final static Pattern PATTERN = Pattern.compile("(\\d+)[^\\d,)]+(\\d+)[^\\d]+(\\d+)\\s*[^NnSs]*\\s*([NnSs])[^\\d]+(\\d+)[^\\d]+(\\d+)[^\\d]+(\\d+)\\s*[^NnSs]*\\s*([EeWwOo])");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoordinatesWithSourceAccuracy parse(LocationSource source, String location) {
        Matcher matcher = getPattern().matcher(location);
        if (matcher.find()) {
            String latDegrees = matcher.group(1);
            String latMinutes = matcher.group(2);
            String latSeconds = matcher.group(3);
            String latNorthSouth = matcher.group(4).toUpperCase();

            String longDegrees = matcher.group(5);
            String longMinutes = matcher.group(6);
            String longSeconds = matcher.group(7);
            String longEastWest = matcher.group(8).toUpperCase();

            Double latitude = LatLongUtils.parseDegMinSecLatitude(latDegrees, latMinutes, latSeconds, latNorthSouth);
            Double longitude = LatLongUtils.parseDegMinSecLongitude(longDegrees, longMinutes, longSeconds, longEastWest);
            return new GlobalCoordinatesWithSourceAccuracy(latitude, longitude, source, LocationAccuracy.LAT_LONG);
        }
        return null;
    }
}
