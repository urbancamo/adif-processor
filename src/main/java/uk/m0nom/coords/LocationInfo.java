package uk.m0nom.coords;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LocationInfo {
    private final LocationAccuracy accuracy;
    private final LocationSource source;

    public LocationInfo(LocationAccuracy accuracy) {
        this.accuracy = accuracy;
        this.source = LocationSource.UNDEFINED;
    }
}
