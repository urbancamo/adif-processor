package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommaSeparatedDecimalWithNsewLatLongParser implements LocationParser, LocationFormatter {
    private final static Pattern PATTERN = Pattern.compile("([+-]*\\d+\\.\\d+)\\s*([NnSs])\\s*,\\s*([+-]*\\d+\\.\\d+)\\s*([EeWwOo])");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoords3D parse(LocationSource source, String location) {
        Matcher matcher = getPattern().matcher(location);
        if (matcher.find()) {
            String latString = matcher.group(1);
            String latNorthSouth = matcher.group(2);
            String longString = matcher.group(3);
            String longEastWest = matcher.group(4);
            Double latitude = LatLongUtils.parseDecimalLatitude(latString, latNorthSouth);
            Double longitude = LatLongUtils.parseDecimalLongitude(longString, longEastWest);
            if (latitude != null && longitude != null) {
                return new GlobalCoords3D(latitude, longitude, source, LocationAccuracy.LAT_LONG);
            }
        }
        return null;
    }

    @Override
    public String format(GlobalCoordinates coords) {
        return String.format("%.6f %s, %.6f %s",
                Math.abs(coords.getLatitude()),
                LatLongUtils.getNorthSouth(coords),
                Math.abs(coords.getLongitude()),
                LatLongUtils.getEastWest(coords));
    }

    @Override
    public String getName() {
        return "Comma Separated Decimal Lat/Long with NSEW indicator";
    }
}
