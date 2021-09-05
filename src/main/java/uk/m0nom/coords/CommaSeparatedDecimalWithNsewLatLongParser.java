package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommaSeparatedDecimalWithNsewLatLongParser implements LatLongParser {
    private final static Pattern PATTERN = Pattern.compile("([+-]*\\d+\\.\\d+)\\s*([NnSs])\\s*,\\s*([+-]*\\d+\\.\\d+)\\s*([EeWw])");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoordinates parse(String latLongString) {
        Matcher matcher = getPattern().matcher(latLongString);
        if (matcher.find()) {
            String latString = matcher.group(1);
            String latNorthSouth = matcher.group(2);
            String longString = matcher.group(3);
            String longEastWest = matcher.group(4);
            Double latitude = LatLongUtils.parseDecimalLatitude(latString, latNorthSouth);
            Double longitude = LatLongUtils.parseDecimalLongitude(longString, longEastWest);
            return new GlobalCoordinates(latitude, longitude);
        }
        return null;
    }
}
