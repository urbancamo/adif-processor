package uk.m0nom.adif3.args;

import lombok.Getter;
import lombok.Setter;

/**
 * Configure the processing of files
 */
@Getter
@Setter
public class TransformControl {
    private Boolean generateKml;
    private Boolean useQrzDotCom;
    private Double myLatitude;
    private Double myLongitude;
    private String myGrid;
    private String pathname;
    private String encoding;

    public TransformControl() {
    }
}
