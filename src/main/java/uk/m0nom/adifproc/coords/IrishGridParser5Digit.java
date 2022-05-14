package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.adifproc.irishgrid.IrishGridConverter;
import uk.m0nom.adifproc.irishgrid.IrishGridConverterResult;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IrishGridParser5Digit implements LocationParser, LocationFormatter {
    private final static Pattern PATTERN = Pattern.compile("^([A-Z]{1})\\s*(\\d{5})\\s*(\\d{5})$");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }

    @Override
    public GlobalCoords3D parse(LocationSource source, String locationString) {
        Matcher matcher = getPattern().matcher(locationString);
        if (matcher.find()) {
            String gridRef = matcher.group(1);
            String easting = matcher.group(2);
            String northing = matcher.group(3);

            String irishGridRef = String.format("%s %s %s", gridRef, easting, northing);
            IrishGridConverter converter = new IrishGridConverter();
            IrishGridConverterResult result = converter.convertIrishGridRefToWsg84(irishGridRef);
            if (result.isSuccess()) {
                return result.getCoords();
            }
        }
        return null;
    }

    @Override
    public String format(GlobalCoordinates coords) {
        IrishGridConverter converter = new IrishGridConverter();
        IrishGridConverterResult result = converter.convertCoordsToIrishGridRef(coords);
        if (result.isSuccess()) {
            return result.getIrishGridRef();
        }
        return "Irish Grid Ref: undefined";
    }

    @Override
    public String getName() {
        return "Irish Grid Ref";
    }
}
