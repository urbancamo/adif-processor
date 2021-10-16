package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
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
        parsers.add(new DegreesDecimalLatLongParser());
        parsers.add(new DegreesDecimalMinutesLatLongParser());
        parsers.add(new DegreesDecimalMinutesWithNsewLatLongParser());
        parsers.add(new CommaSeparatedDecimalLatLongWithAltitudeParser());
        parsers.add(new NsewWithDegreesDecimalLatLongParser());
        parsers.add(new DegreesMinutesSecondsLatLongParser());
        parsers.add(new DegreesMinutesSecondsWithNsewLatLongParser());
        parsers.add(new CommaSeparatedDecimalLatLongParser());
        parsers.add(new CommaSeparatedDecimalWithNsewLatLongParser());
        parsers.add(new DecimalWithNsewLatLongParser());
        parsers.add(new DegreesMinutesDecimalSecondsWithNsewLatLongParser());
        parsers.add(new DegreesMinutesWithNsewLatLongParser());
        parsers.add(new Maidenhead10CharLocatorParser());
        parsers.add(new Maidenhead8CharLocatorParser());
        parsers.add(new Maidenhead6CharLocatorParser());
        // Doesn't work due to WAB references clashing
        // TODO Check This comment!
        parsers.add(new Maidenhead4CharLocatorParser());

        if (databases != null) {
            parsers.add(new WwffLocationParser(databases.getDatabase(ActivityType.WWFF)));
        }
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
