package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DecimalWithNsewLatLongParser implements LocationParser {
    private final static Pattern PATTERN = Pattern.compile("(\\d+\\.\\d+)\\s*([NnSs])\\s*(\\d+\\.\\d+)\\s*([EeWw])");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoordinatesWithAccuracy parse(String latLongString) {
        Matcher matcher = getPattern().matcher(latLongString);
        if (matcher.find()) {
            String latString = matcher.group(1);
            String latNorthSouth = matcher.group(2).toUpperCase();

            String longString = matcher.group(3);
            String longEastWest = matcher.group(4).toUpperCase();

            Double latitude = LatLongUtils.parseDecimalLatitudeWithNs(latString, latNorthSouth);
            Double longitude = LatLongUtils.parseDecimalLongitudeWithEw(longString, longEastWest);
            return new GlobalCoordinatesWithAccuracy(latitude, longitude);
        }
        return null;
    }
}
