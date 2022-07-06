package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AdifLatLongParser  implements LocationParser, LocationFormatter{
    private final static String patternString = "\\s*<%s\\s*LAT:11\\s*>\\s*([NnSs])(\\d{3})\\s+(\\d{2}\\.\\d{3})\\s*<%s\\s*LON:11\\s*>\\s*([EeWw])(\\d{3}) (\\d{2}\\.\\d{3})\\s*";
    private final Pattern pattern;
    private final String adifFieldPrefix;

    private final String formatString = "<%sLAT:11>%s%03.0f %06.3f<%sLON:11>%s%03.0f %06.3f";
    public AdifLatLongParser(String prefix) {
        String adifFieldPrefix1;
        adifFieldPrefix1 = "";
        if (prefix != null) {
            adifFieldPrefix1 = prefix;
        }
        adifFieldPrefix = adifFieldPrefix1;
        pattern = Pattern.compile(String.format(patternString, adifFieldPrefix, adifFieldPrefix));

    }
    @Override
    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public GlobalCoords3D parse(LocationSource source, String location) {
        // replace any CR/LF from the source string and replace with whitespace
        String strippedLocation = location.replace('\n', ' ').replace('\r', ' ');
        Matcher matcher = getPattern().matcher(strippedLocation);
        if (matcher.find()) {
            String latNorthSouth = matcher.group(1).toUpperCase();
            String latDegrees = matcher.group(2);
            String latMinutes = matcher.group(3);

            String longEastWest = matcher.group(4);
            String longDegrees = matcher.group(5);
            String longMinutes = matcher.group(6);

            Double latitude = LatLongUtils.parseDegDecimalMinLatitude(latDegrees, latMinutes, latNorthSouth);
            Double longitude = LatLongUtils.parseDegDecimalMinLongitude(longDegrees, longMinutes, longEastWest);
            if (latitude != null && longitude != null) {
                return new GlobalCoords3D(latitude, longitude, source, LocationAccuracy.LAT_LONG);
            }
        }
        return null;
    }

    @Override
    public String format(GlobalCoordinates coords) {
        return String.format(formatString,
                adifFieldPrefix,
                LatLongUtils.getNorthSouth(coords),
                Math.abs(LatLongUtils.getDegreesLat(coords)),
                Math.abs(LatLongUtils.getMinutesLat(coords)),
                adifFieldPrefix,
                LatLongUtils.getEastWest(coords),
                Math.abs(LatLongUtils.getDegreesLong(coords)),
                Math.abs(LatLongUtils.getMinutesLong(coords)));
    }

    @Override
    public String getName() {
        return "ADIF Format Degrees Minutes Decimal Seconds";
    }
}
