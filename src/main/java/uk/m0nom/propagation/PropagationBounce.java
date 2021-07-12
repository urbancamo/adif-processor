package uk.m0nom.propagation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class PropagationBounce {
    private PropagationMode mode;
    private double distance;
    private double height;
    private double angle;
}
