package uk.m0nom.coords;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LocationParserResult {
    private final GlobalCoordinatesWithSourceAccuracy coords;
    private final LocationParser parser;
}
