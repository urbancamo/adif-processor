package uk.m0nom.callsign;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Callsign {
    private String callsign;
    private CallsignVariant variant;
    private List<Callsign> variants;
    private CallsignSuffix suffix;
    private OperatorLocation location;

    public Callsign(String callsign, CallsignVariant variant) {
        this(callsign, variant, false);
    }

    public Callsign(String callsign, CallsignVariant variant, boolean checkForVariants) {
        this.callsign = callsign.toUpperCase();
        this.variant = variant;
        if (checkForVariants) {
            variants = CallsignUtils.getCallsignVariants(getCallsign());
        }
    }

    @Override
    public String toString() {
        return callsign;
    }
}
