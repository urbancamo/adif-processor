package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class LocationParsingService {
    private final List<LocationParser> parsers = new ArrayList<>();

    /**
     * The order of these parsers is important, they should run from most to least accurate
     */
    public LocationParsingService() {
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
        parsers.add(new AdifLatLongParser("MY_"));
        parsers.add(new AdifLatLongParser(""));
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
        parsers.add(new IrishGridParser5Digit());
    }

    public LocationParserResult parseStringForCoordinates(LocationSource source, String value) {
        String location = value.toUpperCase().trim();
        for (LocationParser parser : parsers) {
            GlobalCoords3D coords = parser.parse(source, location);
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
