package uk.m0nom.satellite;

import java.time.OffsetDateTime;

public interface Satellite {
    String getName();

    SatellitePosition getPosition(OffsetDateTime dateTime);
}
