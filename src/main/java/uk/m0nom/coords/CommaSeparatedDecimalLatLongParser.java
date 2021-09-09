package uk.m0nom.coords;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommaSeparatedDecimalLatLongParser implements LocationParser {
    private final static Pattern PATTERN = Pattern.compile("([+-]*\\d+\\.\\d+)\\s*,\\s*([+-]*\\d+\\.\\d+)");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoordinatesWithLocationSource parse(String location) {
        Matcher matcher = getPattern().matcher(location);
        if (matcher.find()) {
            String latString = matcher.group(1);
            String longString = matcher.group(2);
            Double latitude = LatLongUtils.parseDecimalLatitude(latString);
            Double longitude = LatLongUtils.parseDecimalLongitude(longString);
            return new GlobalCoordinatesWithLocationSource(latitude, longitude);
        }
        return null;
    }
}
