package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.adifproc.osgb36.OsGb36Converter;
import uk.m0nom.adifproc.osgb36.OsGb36ConverterResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OsGb36Parser6Digit implements LocationParser, LocationFormatter{
    private final static Pattern PATTERN = Pattern.compile("^[Ee]\\s*([0-9]{6})[\\s,]*[Nn]\\s*([0-9]{6})$");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoords3D parse(LocationSource source, String locationString) {
        Matcher matcher = getPattern().matcher(locationString);
        if (matcher.find()) {
            String easting = matcher.group(1).replace(" ", "");
            String northing = matcher.group(2).replace(" ", "");
            OsGb36Converter converter = new OsGb36Converter();
            OsGb36ConverterResult result = converter.convertOsGb36EastingNorthingToCoords(easting, northing);
            return new GlobalCoords3D(result.getCoords(), 0.0, new LocationInfo(LocationAccuracy.OSGB36_6DIGIT, LocationSource.OSGB36_CONVERTER));
        }
        return null;
    }

    @Override
    public String format(GlobalCoordinates coords) {
        OsGb36Converter converter = new OsGb36Converter();
        OsGb36ConverterResult result = converter.convertCoordsToOsGb36EastingNorthing(coords);
        if (result.isSuccess()) {
            return String.format("%s %s", result.getOsGb36EastingString(), result.getOsGb36NorthingString());
        }
        return "OSGB36: undefined";
    }

    @Override
    public String getName() {
        return "6 Digit OSGB36";
    }

}
