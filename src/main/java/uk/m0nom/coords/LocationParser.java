package uk.m0nom.coords;

import java.util.regex.Pattern;

public interface LocationParser {
    Pattern getPattern();
    GlobalCoordinatesWithSourceAccuracy parse(LocationSource source, String location);
}
