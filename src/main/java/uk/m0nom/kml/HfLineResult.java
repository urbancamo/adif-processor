package uk.m0nom.kml;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.propagation.PropagationMode;

@Getter
@Setter
public class HfLineResult {
    private int bounces;
    private double altitude;
    private double distance;
    private double angle;
    private double skyDistance;
    private PropagationMode mode;

    public HfLineResult() {
    }
}

