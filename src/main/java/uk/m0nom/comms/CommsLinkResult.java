package uk.m0nom.comms;

import lombok.Getter;
import lombok.Setter;
import org.marsik.ham.adif.enums.Propagation;

@Getter
@Setter
public class CommsLinkResult {
    private int bounces;
    private double altitude;
    private double base;
    private double distance;
    private double fromAngle;
    private double toAngle;
    private double skyDistance;
    private Propagation mode;

    public CommsLinkResult() {
    }
}
