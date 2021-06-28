package uk.m0nom.kml;

import lombok.Getter;
import lombok.Setter;
import uk.m0nom.ionosphere.PropagationMode;

@Getter
@Setter
public class HfLineResult {
    int bounces;
    double altitude;
    double distance;
    double skyDistance;
    PropagationMode mode;

    public HfLineResult() {
    }
}

