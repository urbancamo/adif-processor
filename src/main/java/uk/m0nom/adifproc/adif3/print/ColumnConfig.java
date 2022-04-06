package uk.m0nom.adifproc.adif3.print;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Configuration for a print column to appear in the output
 */
@Data
@NoArgsConstructor
public class ColumnConfig {
    String adif;
    String header;
    int start;
    int length;
    String align;
    String format;
}
