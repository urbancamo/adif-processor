package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.adifproc.irishgrid.IrishGridConverter;
import uk.m0nom.adifproc.irishgrid.IrishGridConverterResult;
import uk.m0nom.adifproc.osgb36.OsGb36Converter;
import uk.m0nom.adifproc.osgb36.OsGb36ConverterResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WabParser implements LocationParser, LocationFormatter{
    // WAB Squares either have a single letter for Northern Island
    private final static Pattern PATTERN = Pattern.compile("^([CDHJ]|[A-Z]{2})\\s*([0-9])\\s*([0-9])$");

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

            // Determine if this is a Northern Island Reference
            if (map.length() == 1) {
                // Northern Ireland
                // Convert to an Irish Grid reference at the centre of the square
                // Need to pad easting and northing with '5's to make them 6 digits
                String locator = String.format("%s%s%s%s%s", map, easting, "5555", northing, "5555");
                IrishGridParser5Digit converter = new IrishGridParser5Digit();
                return converter.parse(LocationSource.WAB, locator);
            } else {
                // UK
                // Convert to an OSGB36 reference at the centre of the square
                // Need to pad easting and northing with '5's to make them 6 digits
                String locator = String.format("%s%s%s%s%s", map, easting, "5555", northing, "5555");
                OsGb36Converter converter = new OsGb36Converter();
                OsGb36ConverterResult result = converter.convertOsGb36ToCoords(locator);
                return new GlobalCoords3D(result.getCoords(), 0.0, new LocationInfo(LocationAccuracy.WAB, LocationSource.WAB));
            }
        }
        return null;
    }

    @Override
    public String format(GlobalCoordinates coords) {
        // Try Irish Grid Reference First
        IrishGridConverter irishConverter = new IrishGridConverter();
        IrishGridConverterResult irishResult = irishConverter.convertCoordsToIrishGridRef(coords);
        if (irishResult.isSuccess()) {
            String gridRef = irishResult.getIrishGridRef();
            return String.format("WAB: %s%s%s", gridRef.substring(0,1), gridRef.substring(2,3), gridRef.substring(8,9));
        } else {
            OsGb36Converter converter = new OsGb36Converter();
            OsGb36ConverterResult result = converter.convertCoordsToOsGb36(coords);
            if (result.isSuccess()) {
                String loc = result.getOsGb36();
                return String.format("WAB: %s%s%s", loc.substring(0, 2), loc.substring(2, 3), loc.substring(7, 8));
            }
        }
        return "WAB Square: undefined";
    }

    @Override
    public String getName() {
        return "WAB Square";
    }

}
