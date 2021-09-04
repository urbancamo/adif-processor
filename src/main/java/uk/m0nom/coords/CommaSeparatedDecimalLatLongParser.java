package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommaSeparatedDecimalLatLongParser implements LatLongParser {
    private final static Pattern PATTERN = Pattern.compile("^\\d+\\.\\d+,\\s*\\d+\\.\\d+$");

    @Override
    public Pattern getPattern() {
        return PATTERN;
    }


    @Override
    public GlobalCoordinates parse(String latLongString) {
        Matcher matcher = getPattern().matcher(latLongString);
        if (matcher.find()) {
            // we have two comma separated floating values
            String[] values = latLongString.split(",");
            if (values.length == 2) {
                Double latitude = LatLongUtils.parseDecimalLatitude(values[0]);
                Double longitude = LatLongUtils.parseDecimalLongitude(values[1]);
            }
        }
        return null;
    }


}
