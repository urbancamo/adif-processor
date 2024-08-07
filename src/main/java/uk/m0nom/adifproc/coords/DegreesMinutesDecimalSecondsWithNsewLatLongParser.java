package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DegreesMinutesDecimalSecondsWithNsewLatLongParser implements LocationParser, LocationFormatter {
    private final static Pattern PATTERN = Pattern.compile("(\\d+)[^\\d]+(\\d+)[^\\d]+(\\d+[\\.,]\\d+)[^NnSs]*([NnSs])[^\\d]+(\\d+)[^\\d]+(\\d+)[^\\d]+(\\d+[\\.,]\\d+)[^EeWwOo]*([EeWwOo])");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoords3D parse(LocationSource source, String location) {
        Matcher matcher = getPattern().matcher(location);
        if (matcher.find()) {
            String latDegrees = matcher.group(1);
            String latMinutes = matcher.group(2);
            String latSeconds = matcher.group(3).replace(',', '.');
            String latNorthSouth = matcher.group(4).toUpperCase();

            String longDegrees = matcher.group(5);
            String longMinutes = matcher.group(6);
            String longSeconds = matcher.group(7).replace(',', '.');
            String longEastWest = matcher.group(8);

            Double latitude = LatLongUtils.parseDegMinSecLatitude(latDegrees, latMinutes, latSeconds, latNorthSouth);
            Double longitude = LatLongUtils.parseDegMinSecLongitude(longDegrees, longMinutes, longSeconds, longEastWest);
            if (latitude != null && longitude != null) {
                return new GlobalCoords3D(latitude, longitude, source, LocationAccuracy.LAT_LONG);
            }
        }
        return null;
    }

    @Override
    public String format(GlobalCoordinates coords) {
        return String.format("%.0f° %.0f' %.3f\"%s %.0f° %.0f' %.3f\"%s",
                Math.abs(LatLongUtils.getDegreesLat(coords)),
                Math.abs(LatLongUtils.getWholeMinutesLat(coords)),
                Math.abs(LatLongUtils.getSecondsLat(coords)),
                LatLongUtils.getNorthSouth(coords),
                Math.abs(LatLongUtils.getDegreesLong(coords)),
                Math.abs(LatLongUtils.getWholeMinutesLong(coords)),
                Math.abs(LatLongUtils.getSecondsLong(coords)),
                LatLongUtils.getEastWest(coords));
    }

    @Override
    public String getName() {
        return "Degrees Minutes Decimal Seconds Lat/Long";
    }
}
