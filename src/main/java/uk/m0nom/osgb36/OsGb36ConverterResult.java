package uk.m0nom.osgb36;

import lombok.Getter;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;

@Getter
@Setter
public class OsGb36ConverterResult {
    private String osGb36;
    private double osGb36Easting;
    private double osGb36Northing;
    private GlobalCoordinates coords;
    private String error;
    private boolean success;

    public String getOsGb36EastingString() {
        return String.format("E %.0f", osGb36Easting);
    }
    public String getOsGb36NorthingString() {
        return String.format("N %.0f", osGb36Northing);
    }
}
