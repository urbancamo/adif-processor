package uk.m0nom.adifproc.irishgrid;

import lombok.Data;
import lombok.NoArgsConstructor;
import uk.m0nom.adifproc.coords.GlobalCoords3D;

@Data
@NoArgsConstructor
public class IrishGridConverterResult {
    private String irishGridRef;
    private double easting;
    private double northing;
    private GlobalCoords3D coords;
    private String error;
    private boolean success;

    public String getEastingString() {
        return String.format("E %.0f", easting);
    }

    public String getNorthingString() {
        return String.format("N %.0f", northing);
    }
}