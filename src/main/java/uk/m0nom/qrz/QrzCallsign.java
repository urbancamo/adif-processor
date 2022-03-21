package uk.m0nom.qrz;

import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Callsign", namespace = QrzNamespace.URI)
public class QrzCallsign {
    private String call; // callsign
    private String xref; // Cross reference: the query callsign that returned this record
    private String aliases; // Other callsigns that resolve to this record
    private String dxcc; // DXCC entity ID (country code) for the callsign
    private String fname; // first name
    private String name; // last name
    private String addr1; // address line 1 (i.e. house # and street)
    private String addr2; // address line 2 (i.e, city name)
    private String state; // state (USA Only)
    private String zip; // Zip/postal code
    private String country; // country name for the QSL mailing address
    private String ccode; // dxcc entity code for the mailing address country
    private Double lat; // latitude of address (signed decimal) S < 0 > N
    private Double lon; // longitude of address (signed decimal) W < 0 > E
    private String grid; // grid locator
    private String county; // county name (USA)
    private String fips; // FIPS county identifier (USA)
    private String land; // DXCC country name of the callsign
    private String efdate; // license effective date (USA)
    private String expdate; // license expiration date (USA)
    private String previousCallsign; // previous callsign
    private String licenseClass; // license class
    private String codes; // license type codes (USA)
    private String qslmgr; // QSL manager info
    private String email; // email address
    private String url; // web page address
    private String webPageViews; // QRZ web page views
    private String bio; // approximate length of the bio HTML in bytes
    private String biodate; // date of the last bio update
    private String image; // full URL of the callsign's primary image
    private String imageinfo; // height:width:size in bytes, of the image file
    private String serial; // QRZ db serial number
    private String moddate; // QRZ callsign last modified date
    private String MSA; // Metro Service Area (USPS)
    private String AreaCode; // Telephone Area Code (USA)
    private String TimeZone; // Time Zone (USA)
    private String GMTOffset; // GMT Time Offset
    private String DST; // Daylight Saving Time Observed
    private String eqsl; // Will accept e-qsl (0/1 or blank if unknown)
    private String mqsl; // Will return paper QSL (0/1 or blank if unknown)
    private String cqzone; // CQ Zone identifier
    private String ituzone; // ITU Zone identifier
    private String born; // operator's year of birth
    private String user; // User who manages this callsign on QRZ
    private String lotw; // Will accept LOTW (0/1 or blank if unknown)
    private String iota; // IOTA Designator (blank if unknown)
    private String geoloc; // Describes source of lat/long data

    //New In Version 1.34
    private String attn; // Attention address line, this line should be prepended to the address
    private String nickname; // A different or shortened name used on the air
    private String name_fmt; // Combined full name and nickname in the format used by QRZ. This format is subject to change.

    public String getCall() {
        return call;
    }

    @XmlElement(name="call", namespace = QrzNamespace.URI)
    public void setCall(String call) {
        this.call = call;
    }

    public String getXref() {
        return xref;
    }

    @XmlElement(name="xref", namespace = QrzNamespace.URI)
    public void setXref(String xref) {
        this.xref = xref;
    }

    public String getAliases() {
        return aliases;
    }

    @XmlElement(name="aliases", namespace = QrzNamespace.URI)
    public void setAliases(String aliases) {
        this.aliases = aliases;
    }

    public String getDxcc() {
        return dxcc;
    }

    @XmlElement(name="dxcc", namespace = QrzNamespace.URI)
    public void setDxcc(String dxcc) {
        this.dxcc = dxcc;
    }

    public String getFname() {
        return fname;
    }

    @XmlElement(name="fname", namespace = QrzNamespace.URI)
    public void setFname(String fname) {
        this.fname = fname;
    }

    public String getName() {
        return name;
    }

    @XmlElement(name="name", namespace = QrzNamespace.URI)
    public void setName(String name) {
        this.name = name;
    }

    public String getAddr1() {
        return addr1;
    }

    @XmlElement(name="addr1", namespace = QrzNamespace.URI)
    public void setAddr1(String addr1) {
        this.addr1 = addr1;
    }

    public String getAddr2() {
        return addr2;
    }

    @XmlElement(name="addr2", namespace = QrzNamespace.URI)
    public void setAddr2(String addr2) {
        this.addr2 = addr2;
    }

    public String getState() {
        return state;
    }

    @XmlElement(name="state", namespace = QrzNamespace.URI)
    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    @XmlElement(name="zip", namespace = QrzNamespace.URI)
    public void setZip(String zip) {
        this.zip = zip;
    }

    public String getCountry() {
        return country;
    }

    @XmlElement(name="country", namespace = QrzNamespace.URI)
    public void setCountry(String country) {
        this.country = country;
    }

    public String getCcode() {
        return ccode;
    }

    @XmlElement(name="ccode", namespace = QrzNamespace.URI)
    public void setCcode(String ccode) {
        this.ccode = ccode;
    }

    public Double getLat() {
        return lat;
    }

    @XmlElement(name="lat", namespace = QrzNamespace.URI)
    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLon() {
        return lon;
    }

    @XmlElement(name="lon", namespace = QrzNamespace.URI)
    public void setLon(Double lon) {
        this.lon = lon;
    }

    public String getGrid() {
        return grid;
    }

    @XmlElement(name="grid", namespace = QrzNamespace.URI)
    public void setGrid(String grid) {
        this.grid = grid;
    }

    public String getCounty() {
        return county;
    }

    @XmlElement(name="country", namespace = QrzNamespace.URI)
    public void setCounty(String county) {
        this.county = county;
    }

    public String getFips() {
        return fips;
    }

    @XmlElement(name="fips", namespace = QrzNamespace.URI)
    public void setFips(String fips) {
        this.fips = fips;
    }

    public String getLand() {
        return land;
    }

    @XmlElement(name="land", namespace = QrzNamespace.URI)
    public void setLand(String land) {
        this.land = land;
    }

    public String getEfdate() {
        return efdate;
    }

    @XmlElement(name="efdate", namespace = QrzNamespace.URI)
    public void setEfdate(String efdate) {
        this.efdate = efdate;
    }

    public String getExpdate() {
        return expdate;
    }

    @XmlElement(name="expdate", namespace = QrzNamespace.URI)
    public void setExpdate(String expdate) {
        this.expdate = expdate;
    }

    public String getPreviousCallsign() {
        return previousCallsign;
    }

    @XmlElement(name="p_call", namespace = QrzNamespace.URI)
    public void setPreviousCallsign(String previousCallsign) {
        this.previousCallsign = previousCallsign;
    }

    public String getLicenseClass() {
        return licenseClass;
    }

    @XmlElement(name="class", namespace = QrzNamespace.URI)
    public void setLicenseClass(String licenseClass) {
        this.licenseClass = licenseClass;
    }

    public String getCodes() {
        return codes;
    }

    @XmlElement(name="codes", namespace = QrzNamespace.URI)
    public void setCodes(String codes) {
        this.codes = codes;
    }

    public String getQslmgr() {
        return qslmgr;
    }

    @XmlElement(name="qslmgr", namespace = QrzNamespace.URI)
    public void setQslmgr(String qslmgr) {
        this.qslmgr = qslmgr;
    }

    public String getEmail() {
        return email;
    }

    @XmlElement(name="email", namespace = QrzNamespace.URI)
    public void setEmail(String email) {
        this.email = email;
    }

    public String getUrl() {
        return url;
    }

    @XmlElement(name="url", namespace = QrzNamespace.URI)
    public void setUrl(String url) {
        this.url = url;
    }

    public String getWebPageViews() {
        return webPageViews;
    }

    @XmlElement(name="u_views", namespace = QrzNamespace.URI)
    public void setWebPageViews(String webPageViews) {
        this.webPageViews = webPageViews;
    }

    public String getBio() {
        return bio;
    }

    @XmlElement(name="bio", namespace = QrzNamespace.URI)
    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getBiodate() {
        return biodate;
    }

    @XmlElement(name="biodate", namespace = QrzNamespace.URI)
    public void setBiodate(String biodate) {
        this.biodate = biodate;
    }

    public String getImage() {
        return image;
    }

    @XmlElement(name="image", namespace = QrzNamespace.URI)
    public void setImage(String image) {
        this.image = image;
    }

    public String getImageinfo() {
        return imageinfo;
    }

    public void setImageinfo(String imageinfo) {
        this.imageinfo = imageinfo;
    }

    @XmlElement(name="serial", namespace = QrzNamespace.URI)
    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    @XmlElement(name="moddate", namespace = QrzNamespace.URI)
    public String getModdate() {
        return moddate;
    }

    public void setModdate(String moddate) {
        this.moddate = moddate;
    }

    @XmlElement(name="msa", namespace = QrzNamespace.URI)
    public String getMSA() {
        return MSA;
    }

    public void setMSA(String MSA) {
        this.MSA = MSA;
    }

    @XmlElement(name="areacode", namespace = QrzNamespace.URI)
    public String getAreaCode() {
        return AreaCode;
    }

    public void setAreaCode(String areaCode) {
        AreaCode = areaCode;
    }

    public String getTimeZone() {
        return TimeZone;
    }

    @XmlElement(name="timezone", namespace = QrzNamespace.URI)
    public void setTimeZone(String timeZone) {
        TimeZone = timeZone;
    }

    public String getGMTOffset() {
        return GMTOffset;
    }

    @XmlElement(name="gmtoffset", namespace = QrzNamespace.URI)
    public void setGMTOffset(String GMTOffset) {
        this.GMTOffset = GMTOffset;
    }

    public String getDST() {
        return DST;
    }

    @XmlElement(name="dst", namespace = QrzNamespace.URI)
    public void setDST(String DST) {
        this.DST = DST;
    }

    public String getEqsl() {
        return eqsl;
    }

    @XmlElement(name="eqsl", namespace = QrzNamespace.URI)
    public void setEqsl(String eqsl) {
        this.eqsl = eqsl;
    }

    public String getMqsl() {
        return mqsl;
    }

    @XmlElement(name="mqsl", namespace = QrzNamespace.URI)
    public void setMqsl(String mqsl) {
        this.mqsl = mqsl;
    }

    public String getCqzone() {
        return cqzone;
    }

    @XmlElement(name="cqzone", namespace = QrzNamespace.URI)
    public void setCqzone(String cqzone) {
        this.cqzone = cqzone;
    }

    public String getItuzone() {
        return ituzone;
    }

    @XmlElement(name="ituzone", namespace = QrzNamespace.URI)
    public void setItuzone(String ituzone) {
        this.ituzone = ituzone;
    }

    public String getBorn() {
        return born;
    }

    @XmlElement(name="born", namespace = QrzNamespace.URI)
    public void setBorn(String born) {
        this.born = born;
    }

    public String getUser() {
        return user;
    }

    @XmlElement(name="user", namespace = QrzNamespace.URI)
    public void setUser(String user) {
        this.user = user;
    }

    public String getLotw() {
        return lotw;
    }

    @XmlElement(name="lotw", namespace = QrzNamespace.URI)
    public void setLotw(String lotw) {
        this.lotw = lotw;
    }

    public String getIota() {
        return iota;
    }

    @XmlElement(name="iota", namespace = QrzNamespace.URI)
    public void setIota(String iota) {
        this.iota = iota;
    }

    public String getGeoloc() {
        return geoloc;
    }

    @XmlElement(name="geoloc", namespace = QrzNamespace.URI)
    public void setGeoloc(String geoloc) {
        this.geoloc = geoloc;
    }

    public String getAttn() {
        return attn;
    }

    @XmlElement(name="attn", namespace = QrzNamespace.URI)
    public void setAttn(String attn) {
        this.attn = attn;
    }

    public String getNickname() {
        return nickname;
    }

    @XmlElement(name="nickname", namespace = QrzNamespace.URI)
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getName_fmt() {
        return name_fmt;
    }

    @XmlElement(name="name_fmt", namespace = QrzNamespace.URI)
    public void setName_fmt(String name_fmt) {
        this.name_fmt = name_fmt;
    }

}
