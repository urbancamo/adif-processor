package uk.m0nom.antenna;

import lombok.*;

/**
 * Defines a named antenna characteristics
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Antenna {
    private AntennaType type;
    private String name;
    private double takeOffAngle;
}
