package uk.m0nom.geocoding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;

@Getter
@Setter
@AllArgsConstructor
public class GeocodingResult {
    private GlobalCoordinatesWithSourceAccuracy coordinates;
    private String matchedOn;
}
