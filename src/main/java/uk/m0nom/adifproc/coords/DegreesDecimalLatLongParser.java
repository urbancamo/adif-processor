package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DegreesDecimalLatLongParser implements LocationParser, LocationFormatter {
    private final static Pattern PATTERN = Pattern.compile("([+-]*)(\\d+\\.\\d+)\\s+([+-]*)(\\d+\\.\\d+)");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoords3D parse(LocationSource source, String location) {
        Matcher matcher = getPattern().matcher(location);

        String latNorthSouth = "N";
        String longEastWest = "E";

        if (matcher.find()) {
            String latSign = matcher.group(1);
            String latDegrees = matcher.group(2);
            if ("-".equals(latSign)) {
                latNorthSouth = "S";
            }
            String longSign = matcher.group(3);
            String longDegrees = matcher.group(4);
            if ("-".equals(longSign)) {
                longEastWest = "W";
            }
            Double latitude = LatLongUtils.parseDegDecimalMinLatitude(latDegrees, "0", latNorthSouth);
            Double longitude = LatLongUtils.parseDegDecimalMinLongitude(longDegrees, "0", longEastWest);
            if (latitude == null || longitude == null) {
                throw new UnsupportedOperationException();
            }
            return new GlobalCoords3D(latitude, longitude, source, LocationAccuracy.LAT_LONG);
        }
        return null;
    }

    @Override
    public String format(GlobalCoordinates coords) {
        return String.format("%.6f %.6f", coords.getLatitude(), coords.getLongitude());
    }

    @Override
    public String getName() {
        return "Decimal Degrees Lat/Long";
    }

}
