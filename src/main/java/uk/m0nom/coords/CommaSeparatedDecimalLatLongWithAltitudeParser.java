package uk.m0nom.coords;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommaSeparatedDecimalLatLongWithAltitudeParser implements LocationParser {
    private final static Pattern PATTERN = Pattern.compile("([+-]*\\d+\\.\\d+)\\s*,\\s*([+-]*\\d+\\.\\d+),\\s*(\\d+)[mM]*");

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
            String altString = matcher.group(3);
            Double latitude = LatLongUtils.parseDecimalLatitude(latString);
            Double longitude = LatLongUtils.parseDecimalLongitude(longString);
            Double altitude = Double.parseDouble(altString);
            if (latitude != null && longitude != null) {
                return new GlobalCoords3D(latitude, longitude, altitude, source, LocationAccuracy.LAT_LONG);
            }
        }
        return null;
    }

    @Override
    public String getName() {
        return "Comma Separated Decimal Lat/Long with Altitude";
    }
}
