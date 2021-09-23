package uk.m0nom.callsign;

import lombok.Getter;

@Getter
public enum CallsignVariant {
    IN_COUNTRY(""),
    HOME_COUNTRY(""),
    G_ALT(""),
    GI_ALT("I"),
    GM_ALT("M"),
    GG_ALT("G"),
    GW_ALT("W"),
    GD_ALT("D");

    private final String modifier;

    CallsignVariant(String modifier) {
        this.modifier = modifier;
    }

}
