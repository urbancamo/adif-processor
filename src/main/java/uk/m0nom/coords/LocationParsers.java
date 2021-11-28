package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class LocationParsers {
    private final List<LocationParser> parsers = new ArrayList<>();

    /**
     * The order of these parsers is important, they should run from most to least accurate
     */
    public LocationParsers() {
        parsers.add(new DegreesDecimalLatLongParser());
        parsers.add(new CommaSeparatedDecimalLatLongParser());
        parsers.add(new DegreesDecimalWithNsewLatLongParser());
        parsers.add(new CommaSeparatedDecimalWithNsewLatLongParser());
        parsers.add(new DegreesDecimalMinutesLatLongParser());
        parsers.add(new DegreesDecimalMinutesWithNsewLatLongParser());
        parsers.add(new CommaSeparatedDecimalLatLongWithAltitudeParser());
        parsers.add(new NsewWithDegreesDecimalLatLongParser());
        parsers.add(new DegreesMinutesSecondsLatLongParser());
        parsers.add(new DegreesMinutesSecondsWithNsewLatLongParser());
        parsers.add(new DegreesMinutesDecimalSecondsWithNsewLatLongParser());
        parsers.add(new DegreesMinutesWithNsewLatLongParser());
        parsers.add(new Maidenhead10CharLocatorParser());
        parsers.add(new Maidenhead8CharLocatorParser());
        parsers.add(new Maidenhead6CharLocatorParser());
        // Doesn't work due to WAB references clashing
        // TODO Check This comment!
        parsers.add(new Maidenhead4CharLocatorParser());
        parsers.add(new OsGb36Parser6Digit());
        parsers.add(new OsGb36Parser5Digit());
        parsers.add(new OsGb36Parser4Digit());
        parsers.add(new OsGb36Parser3Digit());
    }

    public LocationParserResult parseStringForCoordinates(LocationSource source, String value) {
        String location = value.toUpperCase().trim();
        for (LocationParser parser : parsers) {
            GlobalCoordinatesWithSourceAccuracy coords = parser.parse(source, location);
            if (coords != null) {
                return new LocationParserResult(coords, parser);
            }
        }
        return null;
    }

    public List<String> format(GlobalCoordinates coords) {
        List<String> results = new ArrayList<>(10);

        for (LocationParser parser : parsers) {
            if (parser instanceof LocationFormatter) {
                results.add(((LocationFormatter) parser).format(coords));
            }
        }
        return results;
    }
}
