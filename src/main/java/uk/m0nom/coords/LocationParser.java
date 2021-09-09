package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

import java.util.regex.Pattern;

public interface LocationParser {
    Pattern getPattern();
    GlobalCoordinatesWithAccuracy parse(String latLongString);
}
