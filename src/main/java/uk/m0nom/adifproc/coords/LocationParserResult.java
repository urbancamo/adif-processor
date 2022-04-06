package uk.m0nom.adifproc.coords;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LocationParserResult {
    private final GlobalCoords3D coords;
    private final LocationParser parser;
}
