package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DegreesDecimalMinutesWithNsewLatLongParser implements LocationParser, LocationFormatter {
    private final static Pattern PATTERN = Pattern.compile("(\\d+)\\s*°\\s*(\\d+\\.\\d+)\\s*'\\s*([NnSs])\\s*(\\d+)\\s*°\\s*(\\d+\\.\\d+)\\s*'\\s*([EeWwOo])");

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
            String latNorthSouth = matcher.group(3).toUpperCase();

            String longDegrees = matcher.group(4);
            String longMinutes = matcher.group(5);
            String longEastWest = matcher.group(6).toUpperCase();

            Double latitude = LatLongUtils.parseDegDecimalMinLatitude(latDegrees, latMinutes, latNorthSouth);
            Double longitude = LatLongUtils.parseDegDecimalMinLongitude(longDegrees, longMinutes, longEastWest);
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
                Math.abs(LatLongUtils.getMinutesLat(coords)),
                LatLongUtils.getNorthSouth(coords),
                Math.abs(LatLongUtils.getDegreesLong(coords)),
                Math.abs(LatLongUtils.getMinutesLong(coords)),
                LatLongUtils.getEastWest(coords));
    }

    @Override
    public String getName() {
        return "Degrees Decimal Minutes Lat/Long with NSEW indicator";
    }
}
