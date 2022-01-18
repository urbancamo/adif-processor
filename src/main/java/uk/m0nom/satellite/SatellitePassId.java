package uk.m0nom.satellite;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
