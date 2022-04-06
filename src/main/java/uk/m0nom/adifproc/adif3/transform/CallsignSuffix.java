package uk.m0nom.adifproc.adif3.transform;

import lombok.Getter;

/**
 * Recognised callsign suffixes with a human-readable description
 */
@Getter
public enum CallsignSuffix {
    PORTABLE("/P", "Portable", true),
    MOBILE("/M", "Mobile", true),
    MARITIME_MOBILE("/MM", "Maritime Mobile", true),
    PEDESTRIAN_MOBILE("/PM", "Pedestrian Mobile", true),
    ALTERNATIVE("/A", "Alternative QTH", false);

    private final String suffix;
    private final String description;
    private final boolean portable;

    CallsignSuffix(String suffix, String description, boolean portable) {
        this.suffix = suffix;
        this.description = description;
        this.portable = portable;
    }
}
