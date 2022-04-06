package uk.m0nom.adifproc.comms;

import lombok.Getter;
import lombok.Setter;
import org.marsik.ham.adif.enums.Propagation;

import java.util.List;

@Getter
@Setter
public class CommsLinkPath {
    private Propagation mode;
    private List<PropagationApex> hops;
}
