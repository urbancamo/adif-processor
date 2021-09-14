package uk.m0nom.comms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.marsik.ham.adif.enums.Propagation;

@Getter
@Setter
@AllArgsConstructor
public class PropagationBounce {
    private Propagation mode;
    private double distance;
    private double height;
    private double angle;
}
