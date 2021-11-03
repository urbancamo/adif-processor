package uk.m0nom.adif3.control;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.adif3.xsdquery.Adif3Element;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Configure the processing of files
 */
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class TransformControl {
    private String location;

    private String pathname;
    private String outputPath;
    private String encoding;

    private Boolean useQrzDotCom;
    private String qrzUsername;
    private String qrzPassword;

    private boolean stripComment;

    private String printConfigFile;

    private Map<ActivityType, String> activityRefs = new HashMap<>();

    // Active satellites from https://www.eqsl.cc/QSLCard/SatelliteInfo.cfm
    //    Common Name	Alias	NORAD ID	Active
    //    Today?	Commonly
    //            Used1
    //    AISAT-1		44104	Yes	Yes
    //    AO-27	EYESAT-A	22825	Yes	Yes
    //    AO-7	Phase-2B	7530	Yes	Yes
    //    AO-73	AO-73	39444	Yes	Yes
    //    AO-91	Fox-1B AO-91	43017	Yes	Yes
    //    AO-92	AO-92	43137	Yes	Yes
    //    CAS-3H	CAS-3H	40908	Yes	Yes
    //    CAS-4A	ZHUHAI-1 01	42761	Yes	Yes
    //    CAS-4B	ZHUHAI-1 02	42759	Yes	Yes
    //    CAS-6	CAS-6 TIANQIN-1	44881	Yes	Yes
    //    EO-88	FUNcube-5 EO-88	42017	Yes	Yes
    //    FO-29	FO-29	24278	Yes	Yes
    //    FO-99	FO-99	43937	Yes	Yes
    //    FS-3		30776	Yes	Yes
    //    FS-3			Yes	Yes
    //    HO-107	HO-107	45119	Yes	Yes
    //    HO-68	HO-68	36122	Yes	Yes
    //    IO-86	ORARI IO-86	40931	Yes	Yes
    //    ISS		25544	Yes	Yes
    //    JO-97	FUNcube-6 JO-97	43803	Yes	Yes
    //    NO-44	NO-44	26931	Yes	Yes
    //    NO-84	NO-84	40654	Yes	Yes
    //    PO-101	Diwata-2	43678	Yes	Yes
    //    QO-100	Phase-4A Es'Hail-2	43700	Yes	Yes
    //    RS-44	DOSAAF-85	44909	Yes	Yes
    //    SO-50	SaudiSat-1C	27607	Yes	Yes
    //    UVSQ-Sat		47438	Yes	Yes
    //    XW-2A	CAS-3A	40903	Yes	Yes
    //    XW-2B	CAS-3B	40911	Yes	Yes
    //    XW-2C	CAS-3C	40906	Yes	Yes
    //    XW-2D	CAS-3D	40907	Yes	Yes
    //    XW-2E	CAS-3E	40909	Yes	Yes
    //    XW-2F	CAS-3F	40910	Yes	Yes
    private String satelliteName;

    // List of Satellite modes harvested from: https://www.eqsl.cc/QSLCard/SatelliteInfo.cfm
    //    A = Uplink: 2m Downlink: 10m
    //    AU = Uplink: 10m Downlink: 70cm
    //    B = Uplink: 70cm Downlink: 2m
    //    J = Uplink: 2m Downlink: 70cm
    //    K = Uplink: 15m Downlink: 10m
    //    L = Uplink: 23cm Downlink: 70cm
    //    LU = Uplink: 23cm Downlink: 70cm
    //    LV = Uplink: 23cm Downlink: 2m
    //    SX = Uplink: 13cm Downlink: 3cm
    //    T = Uplink: 15m Downlink: 2m
    //    UV = Uplink: 70cm Downlink: 2m
    //    VU = Uplink: 2m Downlink: 70cm
    private String satelliteMode;
    private String satelliteBand;
    private boolean sotaMicrowaveAwardComment;

    private Boolean generateKml;
    private Integer kmlContactWidth;
    private Integer kmlContactTransparency;
    private boolean kmlContactColourByBand;
    private boolean kmlS2s;
    private String kmlS2sContactLineStyle;
    private String kmlContactLineStyle;
    private boolean kmlContactShadow;

    private boolean kmlShowStationSubLabel;
    private boolean kmlShowLocalActivationSites;
    private Double kmlLocalActivationSitesRadius;

    private boolean markdown;
    private boolean contestResults;

    private Double hfAntennaTakeoffAngle;

    private Map<String, String> icons = new HashMap<>();

    private Set<Adif3Element> adif3ElementSet;

    public String getActivityRef(ActivityType type) {
        return activityRefs.get(type);
    }

    public String getIcon(String iconType) {
        return icons.get(iconType);
    }

    public void setIcon(String iconType, String iconUrl) {
        icons.put(iconType, iconUrl);
    }

    public void setActivityRef(ActivityType activity, String ref) {
        activityRefs.put(activity, ref);
    }
}
