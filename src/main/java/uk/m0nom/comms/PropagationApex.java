package uk.m0nom.comms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.ham.adif.enums.Propagation;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PropagationApex {
    /** Mode of propagation */
    private Propagation mode;
    /** Distance across off to point directly below apex */
    private double distanceAcrossEarth;
    /** Distance through the sky from station to apex point */
    private double distanceToApex;
    /** Height of the apex above the Earth (for ducts top of the duct) */
    private double apexHeight;
    /** For a duct specifies the height above the ground of the base of the duct */
    private double baseHeight;
    /** Angle of radiation measured from the horizon, in degrees */
    private double radiationAngle;
}
