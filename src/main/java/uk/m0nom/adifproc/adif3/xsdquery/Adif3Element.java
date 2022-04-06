package uk.m0nom.adifproc.adif3.xsdquery;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * An element in the ADIF3 record
 */
@Getter
@Setter
@AllArgsConstructor
@ToString
public class Adif3Element {
    private final String name;
    private final String type;
}
