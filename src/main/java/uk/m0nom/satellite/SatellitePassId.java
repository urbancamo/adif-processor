package uk.m0nom.satellite;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Satellite pass is identified by the satellite name and date.
 * TODO date probably isn't enough for some satellites - should consider first contact date/time or AOS date/time as an alternative
 */
@Getter
@Setter
@AllArgsConstructor
@EqualsAndHashCode
public class SatellitePassId {
    private String satelliteName;
    private LocalDate date;

    public String toString() {
        return String.format("%s %s", satelliteName, date.toString());
    }
}
