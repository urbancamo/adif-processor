package uk.m0nom.adifproc.geocoding;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import uk.m0nom.adifproc.coords.GlobalCoords3D;

@Getter
@Setter
@AllArgsConstructor
public class GeocodingResult {
    private GlobalCoords3D coordinates;
    private String matchedOn;
    private String error;
}
