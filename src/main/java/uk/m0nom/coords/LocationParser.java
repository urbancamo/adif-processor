package uk.m0nom.coords;

import java.util.regex.Pattern;

public interface LocationParser {
    Pattern getPattern();
    GlobalCoordinatesWithLocationSource parse(String location);
}
