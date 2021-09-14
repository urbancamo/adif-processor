package uk.m0nom.satellite;

import java.time.LocalTime;

public interface Satellite {
    String getName();
    SatellitePosition getPosition(LocalTime dateTime);
}
