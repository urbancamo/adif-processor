package uk.m0nom.adifproc.kml.comms;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * This class contains style URLS for KML code to reference for a line style and associated shadow style
 */
@Getter
@Setter
@AllArgsConstructor
public class KmlCommsStyle {
    private String lineStyleUrl;
    private String shadowStyleUrl;
}
