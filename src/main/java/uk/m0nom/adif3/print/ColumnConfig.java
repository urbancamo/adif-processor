package uk.m0nom.adif3.print;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ColumnConfig {
    String adif;
    String header;
    int start;
    int length;
    String align;
    String format;
}
