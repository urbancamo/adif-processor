package uk.m0nom.satellite;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;

@Getter
@Setter
@AllArgsConstructor
public class SatellitePosition {
    private GlobalCoordinates position;
    private double altitude;
}
