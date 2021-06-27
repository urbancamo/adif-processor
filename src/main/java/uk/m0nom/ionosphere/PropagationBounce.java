package uk.m0nom.ionosphere;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PropagationBounce {
    private double height;
    private double distance;
    private PropagationMode mode;

    public PropagationBounce(PropagationMode mode, double distance, double height) {
        this.mode = mode;
        this.distance = distance;
        this.height = height;
    }
}
