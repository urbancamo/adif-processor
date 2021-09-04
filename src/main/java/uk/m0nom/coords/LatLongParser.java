package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Pattern;

public interface LatLongParser {
    Pattern getPattern();
    GlobalCoordinates parse(String latLongString);
}
