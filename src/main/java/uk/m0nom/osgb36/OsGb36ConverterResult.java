package uk.m0nom.osgb36;

import lombok.Getter;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;

@Getter
@Setter
public class OsGb36ConverterResult {
    private String osGb36;
    private GlobalCoordinates coords;
    private String error;
    private boolean success;
}
