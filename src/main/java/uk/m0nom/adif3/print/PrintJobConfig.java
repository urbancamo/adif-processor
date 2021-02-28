package uk.m0nom.adif3.print;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PrintJobConfig {
    String inEncoding;
    String outEncoding;
    String startCommand;
    String endCommand;

    PageConfig pageConfig;
}
