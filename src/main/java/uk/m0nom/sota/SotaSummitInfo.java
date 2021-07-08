package uk.m0nom.sota;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SotaSummitInfo {
    String summitCode;
    String name;
    double altitude, longitude, latitude;
    int points, bonusPoints;
}
