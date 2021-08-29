package uk.m0nom.adif3.args;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Configure the processing of files
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class TransformControl {
    private String myLatitude;
    private String myLongitude;
    private String myGrid;

    private String pathname;
    private String outputPath;
    private String encoding;

    private Boolean useQrzDotCom;
    private String qrzUsername;
    private String qrzPassword;

    private String hema;
    private String wota;
    private String sota;
    private String pota;
    private String wwff;

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
    private String kmlSotaIconUrl;
    private String kmlWotaIconUrl;
    private String kmlHemaIconUrl;
    private String kmlWwffIconUrl;

    private String kmlCwIconUrl;

    private Boolean kmlShowStationSubLabel;
    private Boolean kmlShowLocalActivationSites;
    private Double kmlLocalActivationSitesRadius;

    private Boolean markdown;
    private Boolean contestResults;

    private Double hfAntennaTakeoffAngle;
}
