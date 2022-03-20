package uk.m0nom.adif3.transform.comment.parsers;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.coords.GlobalCoords3D;

@Getter
@Setter
public class FieldParseResult {
    private String callsign;
    private boolean addToUnmapped;
    private GlobalCoords3D coords;
    private Double latitude, longitude;

    public static FieldParseResult SUCCESS = new FieldParseResult(null, false);

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
