package uk.m0nom.ionosphere;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Propagation {
    private PropagationMode mode;
    private List<PropagationBounce> hops;
}
