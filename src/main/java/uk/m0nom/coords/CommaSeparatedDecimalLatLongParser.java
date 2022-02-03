package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommaSeparatedDecimalLatLongParser implements LocationParser, LocationFormatter {
    private final static Pattern PATTERN = Pattern.compile("([+-]*\\d+\\.\\d+)\\s*,\\s*([+-]*\\d+\\.\\d+)");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoords3D parse(LocationSource source, String location) {
        Matcher matcher = getPattern().matcher(location);
        if (matcher.find()) {
            String latString = matcher.group(1);
            String longString = matcher.group(2);
            Double latitude = LatLongUtils.parseDecimalLatitude(latString);
            Double longitude = LatLongUtils.parseDecimalLongitude(longString);
            return new GlobalCoords3D(latitude, longitude, source, LocationAccuracy.LAT_LONG);
        }
        return null;
    }

    @Override
    public String format(GlobalCoordinates coords) {
        return String.format("%.6f, %.6f", coords.getLatitude(), coords.getLongitude());
    }

    @Override
    public String getName() {
        return "Comma Separated Decimal Lat/Long";
    }
}
