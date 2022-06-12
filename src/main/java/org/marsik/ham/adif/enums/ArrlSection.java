package org.marsik.ham.adif.enums;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

public enum ArrlSection implements AdifEnumCode {

    AL("AL", "Alabama",  291),
    AK("AK", "Alaska",  6),
    AB("AB", "Alberta",  1),
    AR("AR", "Arkansas",  291),
    AZ("AZ", "Arizona",  291),
    BC("BC", "British Columbia",  1),
    CO("CO", "Colorado",  291),
    CT("CT", "Connecticut",  291),
    DE("DE", "Delaware",  291),
    EB("EB", "East Bay",  291),
    EMA("EMA", "Eastern Massachusetts",  291),
    ENY("ENY", "Eastern New York",  291),
    EPA("EPA", "Eastern Pennsylvania",  291),
    EWA("EWA", "Eastern Washington",  291),
    GA("GA", "Georgia",  291),
    GTA("GTA", "Greater Toronto Area",  1, "2012/09/01"),
    ID("ID", "Idaho",  291),
    IL("IL", "Illinois",  291),
    IN("IN", "Indiana",  291),
    IA("IA", "Iowa",  291),
    KS("KS", "Kansas",  291),
    KY("KY", "Kentucky",  291),
    LAX("LAX", "Los Angeles",  291),
    LA("LA", "Louisiana",  291),
    ME("ME", "Maine",  291),
    MB("MB", "Manitoba",  1),
    MAR("MAR", "Maritime",  1),
    MDC("MDC", "Maryland-DC",  291),
    MI("MI", "Michigan",  291),
    MN("MN", "Minnesota",  291),
    MS("MS", "Mississippi",  291),
    MO("MO", "Missouri",  291),
    MT("MT", "Montana",  291),
    NE("NE", "Nebraska",  291),
    NV("NV", "Nevada",  291),
    NH("NH", "New Hampshire",  291),
    NM("NM", "New Mexico",  291),
    NLI("NLI", "New York City-Long Island",  291),
    NL("NL", "Newfoundland/Labrador",  1),
    NC("NC", "North Carolina",  291),
    ND("ND", "North Dakota",  291),
    NTX("NTX", "North Texas",  291),
    NFL("NFL", "Northern Florida",  291),
    NNJ("NNJ", "Northern New Jersey",  291),
    NNY("NNY", "Northern New York",  291),
    NT("NT", "Northwest Territories/Yukon/Nunavut",  1, "2003/11/01"),
    NWT("NWT", "Northwest Territories/Yukon/Nunavut (replaced by NT)", 1, "2003/11/01"),
    OH("OH", "Ohio",  291),
    OK("OK", "Oklahoma",  291),
    ON("ON", "Ontario (replaced by GTA, ONE, ONN, and ONS)",  1, "2012/09/01"),
    ONE("ONE", "Ontario East",  1, 	"2012/09/01"),
    ONN("ONN", "Ontario North",  1, 	"2012/09/01"),
    ONS("ONS", "Ontario South",  1, 	"2012/09/01"),
    ORG("ORG", "Orange",  291),
    OR("OR", "Oregon",  291),
    PAC("PAC", "Pacific",  new Integer[]{9, 20, 103, 110, 123, 134, 138, 166, 174, 197, 297, 515}),
    PR("PR", "Puerto Rico",  new Integer[]{43, 202}),
    QC("QC", "Quebec",  1),
    RI("RI", "Rhode Island",  291),
    SV("SV", "Sacramento Valley",  291),
    SDG("SDG", "San Diego",  291),
    SF("SF", "San Francisco",  291),
    SJV("SJV", "San Joaquin Valley",  291),
    SB("SB", "Santa Barbara",  291),
    SCV("SCV", "Santa Clara Valley",  291),
    SK("SK", "Saskatchewan",  1),
    SC("SC", "South Carolina",  291),
    SD("SD", "South Dakota",  291),
    STX("STX", "South Texas",  291),
    SFL("SFL", "Southern Florida",  291),
    SNJ("SNJ", "Southern New Jersey",  291),
    TN("TN", "Tennessee",  291),
    VI("VI", "US Virgin Islands",  new Integer[]{105, 182, 285}),
    UT("UT", "Utah",  291),
    VT("VT", "Vermont",  291),
    VA("VA", "Virginia",  291),
    WCF("WCF", "West Central Florida",  291),
    WTX("WTX", "West Texas",  291),
    WV("WV", "West Virginia",  291),
    WMA("WMA", "Western Massachusetts",  291),
    WNY("WNY", "Western New York",  291),
    WPA("WPA", "Western Pennsylvania",  291),
    WWA("WWA", "Western Washington",  291),
    WI("WI", "Wisconsin",  291),
    WY("WY", "Wyoming",  291);

    private final String code;

    private final String sectionName;
    private final Integer[] dxccEntityCodes;
    private final ZonedDateTime fromDate;
    private final ZonedDateTime deletedDate;

    ArrlSection(String code, String sectionName, Integer dxccEntityCode) {
        this.code = code;
        this.sectionName = sectionName;
        this.dxccEntityCodes = new Integer[] {dxccEntityCode};
        this.fromDate = null;
        this.deletedDate = null;
    }

    ArrlSection(String code, String sectionName, Integer[] dxccEntityCodes) {
        this.code = code;
        this.sectionName = sectionName;
        this.dxccEntityCodes = dxccEntityCodes;
        this.fromDate = null;
        this.deletedDate = null;
    }

    ArrlSection(String code, String sectionName, Integer dxccEntityCode, String fromDate) {
        this.code = code;
        this.sectionName = sectionName;
        this.dxccEntityCodes = new Integer[] {dxccEntityCode};
        this.fromDate = parseDate(fromDate);
        this.deletedDate = null;
    }

    @Override
    public String adifCode() {
        return code;
    }

    public String getCode() {
        return code;
    }

    public String getSectionName() {
        return sectionName;
    }

    public Integer[] getDxccEntityCodes() {
        return dxccEntityCodes;
    }

    public ZonedDateTime getFromDate() {
        return fromDate;
    }

    public ZonedDateTime getDeletedDate() {
        return deletedDate;
    }

    private final static Map<String, ArrlSection> reverse = new HashMap<>();

    static {
        Stream.of(values()).forEach(v -> reverse.put(v.adifCode(), v));
    }

    public static ArrlSection findByCode(String code) {
        return reverse.get(code.toUpperCase());
    }

    private ZonedDateTime parseDate(String s) {
        return LocalDate.parse(s, DateTimeFormatter.ofPattern("yyyy/MM/dd")).atStartOfDay(ZoneId.of("UTC"));
    }
}
