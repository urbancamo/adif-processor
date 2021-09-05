package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.ArrayList;
import java.util.List;

public class LatLongParsers {
    private List<LatLongParser> parsers = new ArrayList<>();

    public LatLongParsers() {
        parsers.add(new CommaSeparatedDecimalLatLongParser());
        parsers.add(new CommaSeparatedDecimalWithNsewLatLongParser());
        parsers.add(new DecimalWithNsewLatLongParser());
        parsers.add(new DegreesDecimalMinutesLatLongParser());
        parsers.add(new DegreesMinutesSecondsLatLongParser());
    }

    public GlobalCoordinates parseStringLatLong(String value) {
        for (LatLongParser parser : parsers) {
            GlobalCoordinates coords = parser.parse(value);
            if (coords != null) {
                return coords;
            }
        }
        return null;
    }
}
