package uk.m0nom.adif3.args;

import lombok.Getter;
import lombok.Setter;

/**
 * Configure the processing of files
 */
@Getter
@Setter
public class TransformControl {
    private String myLatitude;
    private String myLongitude;
    private String myGrid;

    private String pathname;
    private String encoding;

    private Boolean useQrzDotCom;
    private String qrzUsername;
    private String qrzPassword;

    private String hema;
    private String wota;
    private String sota;

    private Boolean generateKml;
    private Integer kmlContactWidth;
    private Integer kmlContactTransparency;
    private Boolean kmlContactColourByBand;
    private Boolean kmlS2s;
    private String kmlS2sContactLineStyle;
    private String kmlContactLineStyle;
    private Boolean kmlContactShadow;

    private String kmlFixedIconUrl;
    private String kmlPortableIconUrl;
    private String kmlMobileIconUrl;
    private String kmlMaritimeIconUrl;
    private String kmlParkIconUrl;

    public TransformControl() {
    }
}
