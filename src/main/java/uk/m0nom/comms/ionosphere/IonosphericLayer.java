package uk.m0nom.comms.ionosphere;

import lombok.Getter;
import lombok.Setter;

/**
 * A named layer in the Ionosphere
 */
@Getter
@Setter
public class IonosphericLayer {
    private String name;
    private double lower;
    private double upper;

    public IonosphericLayer(String name, double lower, double upper) {
        this.name = name;
        this.lower = lower;
        this.upper = upper;
    }

    public double getAverageHeight() {
        return lower + ((upper - lower) / 2.0);
    }
}
