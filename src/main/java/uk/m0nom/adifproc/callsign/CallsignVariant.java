package uk.m0nom.adifproc.callsign;

import lombok.Getter;

/**
 * UK callsign variants
 */
@Getter
public enum CallsignVariant {
    IN_COUNTRY(""),
    HOME_COUNTRY(""),
    G_ALT(""),
    GE_ALT("E"),
    GQ_ALT("Q"),
    GI_ALT("I"),
    GM_ALT("M"),
    GG_ALT("G"),
    GW_ALT("W"),
    GD_ALT("D"),
    GR_ALT("R");

    private final String modifier;

    CallsignVariant(String modifier) {
        this.modifier = modifier;
    }

}
