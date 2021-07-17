package uk.m0nom.callsign;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum CallsignSuffix {
    PORTABLE("/P"),
    MOBILE("/M"),
    MARITIME_MOBILE("/MM"),
    PEDESTRIAN_MOBILE("/PM"),
    ALTERNATIVE_ADDRESS("/A");

    private String suffix;
}
