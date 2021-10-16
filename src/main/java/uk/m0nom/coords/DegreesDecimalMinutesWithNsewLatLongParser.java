package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DegreesDecimalMinutesWithNsewLatLongParser implements LocationParser, LocationFormatter {
    private final static Pattern PATTERN = Pattern.compile("(\\d+\\.\\d+)[°]*([NnSs])\\s+(\\d+\\.\\d+)[°]*([EeWwOo])");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoordinatesWithSourceAccuracy parse(LocationSource source, String location) {
        Matcher matcher = getPattern().matcher(location);

        if (matcher.find()) {
            String latDegrees = matcher.group(1);
            String latNorthSouth = matcher.group(2).toUpperCase();

            String longDegrees = matcher.group(3);
            String longEastWest = matcher.group(4).toUpperCase();

            Double latitude = LatLongUtils.parseDegDecimalMinLatitude(latDegrees, "0", latNorthSouth);
            Double longitude = LatLongUtils.parseDegDecimalMinLongitude(longDegrees, "0", longEastWest);
            if (latitude == null || longitude == null) {
                throw new UnsupportedOperationException();
            }
            return new GlobalCoordinatesWithSourceAccuracy(latitude, longitude, source, LocationAccuracy.LAT_LONG);
        }
        return null;
    }


    @Override
    public String format(GlobalCoordinates coords) {
        return String.format("%.0f° %.3f' %s, %.0f° %.3f' %s",
                Math.abs(LatLongUtils.getDegreesLat(coords)),
                LatLongUtils.getMinutesLat(coords),
                LatLongUtils.getNorthSouth(coords),
                Math.abs(LatLongUtils.getDegreesLong(coords)),
                LatLongUtils.getMinutesLong(coords),
                LatLongUtils.getEastWest(coords));
    }

    @Override
    public String getName() {
        return "Degrees Decimal Minutes with NSEW";
    }
}
