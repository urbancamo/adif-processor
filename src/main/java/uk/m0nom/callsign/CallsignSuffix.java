package uk.m0nom.callsign;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * The supported callsign suffixes
 */
@Getter
@AllArgsConstructor
public enum CallsignSuffix {
    PORTABLE("/P", "Portable"),
    MOBILE("/M", "Mobile"),
    MARITIME_MOBILE("/MM", "Maritime Mobile"),
    PEDESTRIAN_MOBILE("/PM", "Pedestrian Mobile"),
    ALTERNATIVE_ADDRESS("/A", "Alternate Address"),
    QRP("/QRP", "QRP Low Power");

    private final String suffix;
    private final String description;
}
