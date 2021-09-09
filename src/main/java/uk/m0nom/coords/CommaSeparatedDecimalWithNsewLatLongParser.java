package uk.m0nom.coords;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommaSeparatedDecimalWithNsewLatLongParser implements LocationParser {
    private final static Pattern PATTERN = Pattern.compile("([+-]*\\d+\\.\\d+)\\s*([NnSs])\\s*,\\s*([+-]*\\d+\\.\\d+)\\s*([EeWwOo])");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoordinatesWithLocationSource parse(String location) {
        Matcher matcher = getPattern().matcher(location);
        if (matcher.find()) {
            String latString = matcher.group(1);
            String latNorthSouth = matcher.group(2);
            String longString = matcher.group(3);
            String longEastWest = matcher.group(4);
            Double latitude = LatLongUtils.parseDecimalLatitude(latString, latNorthSouth);
            Double longitude = LatLongUtils.parseDecimalLongitude(longString, longEastWest);
            return new GlobalCoordinatesWithLocationSource(latitude, longitude);
        }
        return null;
    }
}
