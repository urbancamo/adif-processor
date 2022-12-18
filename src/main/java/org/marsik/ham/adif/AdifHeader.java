package org.marsik.ham.adif;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Data
@Getter
@Setter
public class AdifHeader {
    String preamble = "";
    String version = "3.1.4";
    String programId;
    String programVersion;
    ZonedDateTime timestamp;
}
