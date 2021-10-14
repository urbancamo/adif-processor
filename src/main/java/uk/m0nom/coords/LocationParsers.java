package uk.m0nom.coords;

import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;

import java.util.ArrayList;
import java.util.List;

public class LocationParsers {
    private final List<LocationParser> parsers = new ArrayList<>();

    /**
     * The order of these parsers is important, they should run from most to least accurate
     */
    public LocationParsers(ActivityDatabases databases) {
        parsers.add(new CommaSeparatedDecimalLatLongWithAltitudeParser());
        parsers.add(new DegreesDecimalWithNsewLatLongParser());
        parsers.add(new NsewWithDegreesDecimalLatLongParser());
        parsers.add(new DegreesDecimalLatLongParser());
        parsers.add(new DegreesMinutesSecondsLatLongParser());
        parsers.add(new CommaSeparatedDecimalLatLongParser());
        parsers.add(new CommaSeparatedDecimalWithNsewLatLongParser());
        parsers.add(new DecimalWithNsewLatLongParser());
        parsers.add(new DegreesDecimalMinutesLatLongParser());
        parsers.add(new DegreesMinutesDecimalSecondsLatLongParser());
        parsers.add(new DegreesMinutesWithNsewLatLongParser());
        parsers.add(new Maidenhead10CharLocatorParser());
        parsers.add(new Maidenhead8CharLocatorParser());
        parsers.add(new Maidenhead6CharLocatorParser());
        // Doesn't work due to WAB references clashing
        // parsers.add(new Maidenhead4CharLocatorParser());
        parsers.add(new WwffLocationParser(databases.getDatabase(ActivityType.WWFF)));
    }

    public GlobalCoordinatesWithSourceAccuracy parseStringForCoordinates(LocationSource source, String value) {
        for (LocationParser parser : parsers) {
            GlobalCoordinatesWithSourceAccuracy coords = parser.parse(source, value);
            if (coords != null) {
                return coords;
            }
        }
        return null;
    }
}
