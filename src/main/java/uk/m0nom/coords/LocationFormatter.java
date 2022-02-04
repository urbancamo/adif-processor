package uk.m0nom.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

public interface LocationFormatter {
    String format(GlobalCoordinates coords);
    String getName();
}
