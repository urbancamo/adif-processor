package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.adifproc.coords.GlobalCoords3D;

@Getter
@Setter
public class FieldParseResult {
    private String callsign;
    private boolean addToUnmapped;
    private GlobalCoords3D coords;
    private Double latitude, longitude;
    private String warning;

    public static FieldParseResult SUCCESS = new FieldParseResult(null, false);

    public FieldParseResult(String warning) {
        setCallsign(null);
        setAddToUnmapped(false);
        setWarning(warning);
    }

    public FieldParseResult(String callsign, boolean addToUnmapped) {
        setCallsign(callsign);
        setAddToUnmapped(addToUnmapped);
    }

    public FieldParseResult(GlobalCoords3D coords) {
        setCoords(coords);
    }

    public FieldParseResult(Double latitude, Double longitude) {
        setLatitude(latitude);
        setLongitude(longitude);
    }
}
