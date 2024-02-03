package uk.m0nom.adifproc.antenna;

import lombok.*;
import org.jetbrains.annotations.NotNull;

/**
 * Defines a named antenna characteristics
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Antenna implements Comparable<Antenna> {
    private AntennaType type;
    private String name;
    private double takeOffAngle;

    @Override
    public int compareTo(@NotNull Antenna o) {
        return Double.valueOf(takeOffAngle).compareTo(o.getTakeOffAngle());
    }

    public String getDescription() {
        return String.format("%s, max power at %.0f°", name, takeOffAngle);
    }
}
