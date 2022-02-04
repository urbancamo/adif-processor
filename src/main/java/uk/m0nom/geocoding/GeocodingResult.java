package uk.m0nom.geocoding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.m0nom.coords.GlobalCoords3D;

@Getter
@Setter
@AllArgsConstructor
public class GeocodingResult {
    private GlobalCoords3D coordinates;
    private String matchedOn;
    private String error;
}
