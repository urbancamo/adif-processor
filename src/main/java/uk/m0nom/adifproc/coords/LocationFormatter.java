package uk.m0nom.adifproc.coords;

import org.gavaghan.geodesy.GlobalCoordinates;

public interface LocationFormatter {
    String format(GlobalCoordinates coords);
    String getName();
}
