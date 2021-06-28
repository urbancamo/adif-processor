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
    private String myLatitude;
    private String myLongitude;
    private String myGrid;
    private String pathname;
    private String encoding;
    private Boolean kmlS2s;
    private String kmlS2sContactLineStyle;
    private String kmlContactLineStyle;
    private String qrzUsername;
    private String qrzPassword;
    private Boolean kmlContactShadow;
    private String kmlFixedIconUrl;
    private String kmlPortableIconUrl;
    private String kmlMobileIconUrl;
    private String kmlMaritimeIconUrl;


    public TransformControl() {
    }
}
