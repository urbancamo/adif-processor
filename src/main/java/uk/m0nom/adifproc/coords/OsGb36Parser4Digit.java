package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.adifproc.osgb36.OsGb36Converter;
import uk.m0nom.adifproc.osgb36.OsGb36ConverterResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OsGb36Parser4Digit implements LocationParser, LocationFormatter{
    private final static Pattern PATTERN = Pattern.compile("^([A-Z]{2})\\s*([0-9]{4})\\s*([0-9]{4})$");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoords3D parse(LocationSource source, String locationString) {
        Matcher matcher = getPattern().matcher(locationString);
        if (matcher.find()) {
            String map = matcher.group(1);
            String easting = matcher.group(2);
            String northing = matcher.group(3);
            // Need to pad easting and northing with '5's to make them 6 digits
            String locator = String.format("%s%s%s%s%s", map, easting, "55", northing, "55");
            OsGb36Converter converter = new OsGb36Converter();
            OsGb36ConverterResult result = converter.convertOsGb36ToCoords(locator);
            return new GlobalCoords3D(result.getCoords(), 0.0, new LocationInfo(LocationAccuracy.OSGB36_4DIGIT, LocationSource.OSGB36_CONVERTER));
        }
        return null;
    }

    @Override
    public String format(GlobalCoordinates coords) {
        OsGb36Converter converter = new OsGb36Converter();
        OsGb36ConverterResult result = converter.convertCoordsToOsGb36(coords);
        if (result.isSuccess()) {
            String loc = result.getOsGb36();
            return String.format("%s %s %s", loc.substring(0, 2), loc.substring(2, 6), loc.substring(7, 11));
        }
        return "OSGB36: undefined";
    }

    @Override
    public String getName() {
        return "4 Digit OSGB36";
    }

}
