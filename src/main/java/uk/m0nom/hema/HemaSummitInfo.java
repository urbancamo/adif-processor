package uk.m0nom.hema;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class HemaSummitInfo {
    int key;
    String summitCode;
    String name;
    double altitude, latitude, longitude;
    boolean active;
}
