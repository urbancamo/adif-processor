package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.ArrayList;
import java.util.List;

public class LocationParsers {
    private List<LocationParser> parsers = new ArrayList<>();

    public LocationParsers() {
        parsers.add(new CommaSeparatedDecimalLatLongParser());
        parsers.add(new CommaSeparatedDecimalWithNsewLatLongParser());
        parsers.add(new DecimalWithNsewLatLongParser());
        parsers.add(new DegreesDecimalMinutesLatLongParser());
        parsers.add(new DegreesMinutesSecondsLatLongParser());
        parsers.add(new DegreesMinutesDecimalSecondsLatLongParser());
        parsers.add(new MaidenheadLocatorParser());
    }

    public GlobalCoordinates parseStringLatLong(String value) {
        for (LocationParser parser : parsers) {
            GlobalCoordinates coords = parser.parse(value);
            if (coords != null) {
                return coords;
            }
        }
        return null;
    }
}
