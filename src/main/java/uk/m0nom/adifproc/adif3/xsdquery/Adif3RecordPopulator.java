package uk.m0nom.adifproc.adif3.xsdquery;

import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.*;
import org.marsik.ham.adif.types.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Logger;

public class Adif3RecordPopulator {
    private static final Logger logger = Logger.getLogger(Adif3RecordPopulator.class.getName());
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("hhmm");

    public static void addFieldToRecord(Adif3Record rec, Adif3Field field, String value) {
        switch (field.getName()) {
            case "ADDRESS":
                rec.setAddress(value);
                break;
            case "ADDRESS_INTL":
                rec.setAddressIntl(value);
                break;
            case "AGE":
                rec.setAge(Integer.valueOf(value));
                break;
            case "ALTITUDE":
                rec.setAltitude(Double.valueOf(value));
                break;
            case "ANT_AZ":
                rec.setAntAz(Double.valueOf(value));
                break;
            case "ANT_EL":
                rec.setAntEl(Double.valueOf(value));
                break;
            case "ANT_PATH":
                rec.setAntPath(AntPath.findByCode(value));
                break;
            case "ARRL_SECT":
                rec.setArrlSect(value);
                break;
            case "AWARD_GRANTED":
                rec.setAwardGranted(toList(value));
                break;
            case "AWARD_SUBMITTED":
                rec.setAwardSubmitted(toList(value));
                break;
            case "A_INDEX":
                rec.setAIndex(Double.valueOf(value));
                break;
            case "BAND":
                rec.setBand(Band.findByCode(value));
                break;
            case "BAND_RX":
                rec.setBandRx(Band.findByCode(value));
                break;
            case "CALL":
                rec.setCall(value);
                break;
            case "CHECK":
                rec.setCheck(value);
                break;
            case "CLASS":
                rec.setContestClass(value);
                break;
            case "CLUBLOG_QSO_UPLOAD_DATE":
                rec.setClublogQsoUploadDate(toZonedDateTime(value));
                break;
            case "CLUBLOG_QSO_UPLOAD_STATUS":
                rec.setClublogQsoUploadStatus(QsoUploadStatus.findByCode(value));
                break;
            case "CNTY":
                rec.setCnty(value);
                break;
            case "COMMENT":
                rec.setComment(value);
                break;
            case "COMMENT_INTL":
                rec.setCommentIntl(value);
                break;
            case "CONT":
                rec.setCont(Continent.findByCode(value));
                break;
            case "CONTACTED_OP":
                rec.setContactedOp(value);
                break;
            case "CONTEST_ID":
                rec.setContestId(value);
                break;
            case "COUNTRY":
                rec.setCountry(value);
                break;
            case "COUNTRY_INTL":
                rec.setCountryIntl(value);
                break;
            case "CQZ":
                rec.setCqz(Integer.valueOf(value));
                break;
            case "CREDIT_GRANTED":
                rec.setCreditGranted(toList(value));
                break;
            case "CREDIT_SUBMITTED":
                rec.setCreditSubmitted(toList(value));
                break;
            case "DARC_DOK":
                rec.setDarcDok(value);
                break;
            case "DISTANCE":
                rec.setDistance(Double.valueOf(value));
                break;
            case "DXCC":
                rec.setDxcc(Integer.valueOf(value));
                break;
            case "EMAIL":
                rec.setEmail(value);
                break;
            case "EQSL_QSLRDATE":
                rec.setEqslQslRDate(toZonedDateTime(value));
                break;
            case "EQSL_QSLSDATE":
                rec.setEqslQslSDate(toZonedDateTime(value));
                break;
            case "EQSL_QSL_RCVD":
                rec.setEqslQslRcvd(QslRcvd.findByCode(value));
                break;
            case "EQSL_QSL_SENT":
                rec.setEqslQslSent(QslSent.findByCode(value));
                break;
            case "EQ_CALL":
                rec.setEqCall(value);
                break;
            case "FISTS":
                rec.setFists(value);
                break;
            case "FISTS_CC":
                rec.setFistsCc(value);
                break;
            case "FORCE_INIT":
                rec.setForceInt(Boolean.valueOf(value));
                break;
            case "FREQ":
                rec.setFreq(Double.valueOf(value));
                break;
            case "FREQ_RX":
                rec.setFreqRx(Double.valueOf(value));
                break;
            case "GRIDSQUARE":
                rec.setGridsquare(value);
                break;
            case "GRIDSQUARE_EXT":
                rec.setGridsquareExt(value);
                break;
            case "HAMLOGEU_QSO_UPLOAD_DATE":
                rec.setHamlogEuQsoUploadDate(toZonedDateTime(value));
                break;
            case "HAMLOGEU_QSO_UPLOAD_STATUS":
                rec.setHamlogEuQsoUploadStatus(QsoUploadStatus.findByCode(value));
                break;
            case "HAMQTH_QSO_UPLOAD_DATE":
                rec.setHamqthQsoUploadDate(toZonedDateTime(value));
                break;
            case "HAMQTH_QSO_UPLOAD_STATUS":
                rec.setHamqthQsoUploadStatus(QsoUploadStatus.findByCode(value));
                break;
            case "HRDLOG_QSO_UPLOAD_DATE":
                rec.setHrdlogQsoUploadDate(toZonedDateTime(value));
                break;
            case "HRDLOG_QSO_UPLOAD_STATUS":
                rec.setHrdlogQsoUploadStatus(QsoUploadStatus.findByCode(value));
                break;
            case "IOTA":
                rec.setIota(Iota.findByCode(value));
                break;
            case "IOTA_ISLAND_ID":
                rec.setIotaIslandId(value);
                break;
            case "ITUZ":
                rec.setItuz(Integer.valueOf(value));
                break;
            case "K_INDEX":
                rec.setKIndex(Double.valueOf(value));
                break;
            case "LAT":
                setCoordinates(rec, Double.valueOf(value), null);
                break;
            case "LON":
                setCoordinates(rec, null, Double.valueOf(value));
                break;
            case "LOTW_QSLRDATE":
                rec.setLotwQslRDate(toZonedDateTime(value));
                break;
            case "LOTW_QSLSDATE":
                rec.setLotwQslSDate(toZonedDateTime(value));
                break;
            case "LOTW_QSL_RCVD":
                rec.setLotwQslRcvd(QslRcvd.findByCode(value));
                break;
            case "LOTW_QSL_SENT":
                rec.setLotwQslSent(QslSent.findByCode(value));
                break;
            case "MAX_BURSTS":
                rec.setMaxBursts(Integer.valueOf(value));
                break;
            case "MODE":
                rec.setMode(Mode.findByCode(value));
                break;
            case "MS_SHOWER":
                rec.setMsShower(value);
                break;
            case "MY_ALTITUDE":
                rec.setMyAltitude(Double.valueOf(value));
                break;
            case "MY_ANTENNA":
                rec.setMyAntenna(value);
                break;
            case "MY_ANTENNA_INTL":
                rec.setMyAntennaIntl(value);
                break;
            case "MY_ARRL_SECT":
                rec.setMyArrlSect(value);
                break;
            case "MY_CITY":
                rec.setMyCity(value);
                break;
            case "MY_CITY_INTL":
                rec.setMyCityIntl(value);
                break;
            case "MY_CNTY":
                rec.setMyCnty(value);
                break;
            case "MY_COUNTRY":
                rec.setMyCountry(value);
                break;
            case "MY_COUNTRY_INTL":
                rec.setMyCountryIntl(value);
                break;
            case "MY_CQ_ZONE":
                rec.setMyCqZone(Integer.valueOf(value));
                break;
            case "MY_DXCC":
                rec.setMyDxcc(Integer.valueOf(value));
                break;
            case "MY_FISTS":
                rec.setMyFists(value);
                break;
            case "MY_GRIDSQUARE":
                rec.setMyGridSquare(value);
                break;
            case "MY_GRIDSQUARE_EXT":
                rec.setMyGridsquareExt(value);
                break;
            case "MY_IOTA":
                rec.setMyIota(Iota.findByCode(value));
                break;
            case "MY_IOTA_ISLAND_ID":
                rec.setMyIotaIslandId(value);
                break;
            case "MY_ITU_ZONE":
                rec.setMyItuZone(Integer.valueOf(value));
                break;
            case "MY_LAT":
                setMyCoordinates(rec, Double.valueOf(value), null);
                break;
            case "MY_LON":
                setMyCoordinates(rec, null, Double.valueOf(value));
                break;
            case "MY_NAME":
                rec.setMyName(value);
                break;
            case "MY_NAME_INTL":
                rec.setMyNameIntl(value);
                break;
            case "MY_POSTAL_CODE":
                rec.setMyPostalCode(value);
                break;
            case "MY_POSTAL_CODE_INTL":
                rec.setMyPostalCodeIntl(value);
                break;
            case "MY_POTA_REF":
                rec.setMyPotaRef(toPotaList(value));
                break;
            case "MY_RIG":
                rec.setMyRig(value);
                break;
            case "MY_RIG_INTL":
                rec.setMyRigIntl(value);
                break;
            case "MY_SIG":
                rec.setMySig(value);
                break;
            case "MY_SIG_INFO":
                rec.setMySigInfo(value);
                break;
            case "MY_SIG_INFO_INTL":
                rec.setMySigInfoIntl(value);
                break;
            case "MY_SIG_INTL":
                rec.setMySigIntl(value);
                break;
            case "MY_SOTA_REF":
                rec.setMySotaRef(Sota.valueOf(value));
                break;
            case "MY_STATE":
                rec.setMyState(value);
                break;
            case "MY_STREET":
                rec.setMyStreet(value);
                break;
            case "MY_STREET_INTL":
                rec.setMyStreetIntl(value);
                break;
            case "MY_USACA_COUNTIES":
                rec.setMyUsaCaCounties(toList(value));
                break;
            case "MY_VUCC_GRIDS":
                rec.setMyVuccGrids(toList(value));
                break;
            case "MY_WWFF_REF":
                rec.setMyWwffRef(Wwff.valueOf(value));
                break;
            case "NAME":
                rec.setName(value);
                break;
            case "NAME_INTL":
                rec.setNameIntl(value);
                break;
            case "NOTES":
                rec.setNotes(value);
                break;
            case "NOTES_INTL":
                rec.setNotesIntl(value);
                break;
            case "NR_BURSTS":
                rec.setNrBursts(Integer.valueOf(value));
                break;
            case "NR_PINGS":
                rec.setNrPings(Integer.valueOf(value));
                break;
            case "OPERATOR":
                rec.setOperator(value);
                break;
            case "OWNER_CALLSIGN":
                rec.setOwnerCallsign(value);
                break;
            case "PFX":
                rec.setPfx(value);
                break;
            case "POTA_REF":
                rec.setPotaRef(toPotaList(value));
                break;
            case "PRECEDENCE":
                rec.setPrecedence(value);
                break;
            case "PROP_MODE":
                rec.setPropMode(Propagation.findByCode(value));
                break;
            case "PUBLIC_KEY":
                rec.setPublicKey(value);
                break;
            case "QRZCOM_QSO_UPLOAD_DATE":
                rec.setQrzcomQsoUploadDate(toZonedDateTime(value));
                break;
            case "QRZCOM_QSO_UPLOAD_STATUS":
                rec.setQrzcomQsoUploadStatus(QsoUploadStatus.findByCode(value));
                break;
            case "QSLMSG":
                rec.setQslMsg(value);
                break;
            case "QSLMSG_INTL":
                rec.setQslMsgIntl(value);
                break;
            case "QSLRDATE":
                rec.setQslRDate(toLocalDate(value));
                break;
            case "QSLSDATE":
                rec.setQslSDate(toLocalDate(value));
                break;
            case "QSL_RCVD":
                rec.setQslRcvd(QslRcvd.findByCode(value));
                break;
            case "QSL_RCVD_VIA":
                rec.setQslRcvdVia(QslVia.findByCode(value));
                break;
            case "QSL_SENT":
                rec.setQslSent(QslSent.findByCode(value));
                break;
            case "QSL_SENT_VIA":
                rec.setQslSentVia(QslVia.findByCode(value));
                break;
            case "QSL_VIA":
                rec.setQslVia(value);
                break;
            case "QSO_COMPLETE":
                rec.setQsoComplete(QsoComplete.valueOf(value));
                break;
            case "QSO_DATE":
                rec.setQsoDate(toZonedDateTime(value));
                break;
            case "QSO_DATE_OFF":
                rec.setQsoDateOff(toZonedDateTime(value));
                break;
            case "QSO_RANDOM":
                rec.setQsoRandom(toBoolean(value));
                break;
            case "QTH":
                rec.setQth(value);
                break;
            case "QTH_INTL":
                rec.setQthIntl(value);
                break;
            case "REGION":
                rec.setRegion(value);
                break;
            case "RIG":
                rec.setRig(value);
                break;
            case "RIG_INTL":
                rec.setRigIntl(value);
                break;
            case "RST_RCVD":
                rec.setRstRcvd(value);
                break;
            case "RST_SENT":
                rec.setRstSent(value);
                break;
            case "RX_PWR":
                rec.setRxPwr(Double.valueOf(value));
                break;
            case "SAT_MODE":
                rec.setSatMode(value);
                break;
            case "SAT_NAME":
                rec.setSatName(value);
                break;
            case "SFI":
                rec.setSfi(Double.valueOf(value));
                break;
            case "SIG":
                rec.setSig(value);
                break;
            case "SIG_INFO":
                rec.setSigInfo(value);
                break;
            case "SIG_INFO_INTL":
                rec.setSigInfoIntl(value);
                break;
            case "SIG_INTL":
                rec.setSigIntl(value);
                break;
            case "SILENT_KEY":
                rec.setSilentKey(toBoolean(value));
                break;
            case "SKCC":
                rec.setSkcc(value);
                break;
            case "SOTA_REF":
                rec.setSotaRef(Sota.valueOf(value));
                break;
            case "SRX":
                rec.setSrx(Integer.valueOf(value));
                break;
            case "SRX_STRING":
                rec.setSrxString(value);
                break;
            case "STATE":
                rec.setState(value);
                break;
            case "STATION_CALLSIGN":
                rec.setStationCallsign(value);
                break;
            case "STX":
                rec.setStx(Integer.valueOf(value));
                break;
            case "STX_STRING":
                rec.setStxString(value);
                break;
            case "SUBMODE":
                rec.setSubmode(value);
                break;
            case "SWL":
                rec.setSwl(toBoolean(value));
                break;
            case "TEN_TEN":
                rec.setTenTen(Integer.valueOf(value));
                break;
            case "TIME_OFF":
                rec.setTimeOff(toLocalTime(value));
                break;
            case "TIME_ON":
                rec.setTimeOn(toLocalTime(value));
                break;
            case "TX_PWR":
                rec.setTxPwr(Double.valueOf(value));
                break;
            case "UKSMG":
                rec.setUksmg(Integer.valueOf(value));
                break;
            case "USACA_COUNTIES":
                rec.setUsaCaCounties(toList(value));
                break;
            case "VUCC_GRIDS":
                rec.setVuccGrids(toList(value));
                break;
            case "WEB":
                rec.setWeb(value);
                break;
            case "WWFF_REF":
                rec.setWwffRef(Wwff.valueOf(value));
                break;
            case "APP":
            case "USERDEF":
                logger.warning(String.format("Setting ADIF3 Field %s not supported (value: %s)", field.getName(), value));
                break;

        }
    }

    private static PotaList toPotaList(String value) {
        PotaList potaList = new PotaList();
        for (String potaRef: StringUtils.split(value, ",")) {
            potaList.addPota(Pota.valueOf(potaRef));
        }
        return potaList;
    }

    private static List<String> toList(String value) {
        return Arrays.asList(StringUtils.split(value, ","));
    }

    private static void setCoordinates(Adif3Record rec, Double lat, Double lon) {
        if (rec.getCoordinates() != null) {
            if (lat != null) {
                rec.getCoordinates().setLatitude(lat);
            }
            if (lon != null) {
                rec.getCoordinates().setLongitude(lon);
            }
        } else {
            rec.setCoordinates(new GlobalCoordinates(lat, lon));
        }
    }

    private static void setMyCoordinates(Adif3Record rec, Double lat, Double lon) {
        if (rec.getMyCoordinates() != null) {
            if (lat != null) {
                rec.getMyCoordinates().setLatitude(lat);
            }
            if (lon != null) {
                rec.getMyCoordinates().setLongitude(lon);
            }
        } else {
            rec.setMyCoordinates(new GlobalCoordinates(lat, lon));
        }
    }

    private static ZonedDateTime toZonedDateTime(String value) {
        return ZonedDateTime.parse(value, dateTimeFormatter);
    }

    private static LocalDate toLocalDate(String value) {
        return LocalDate.parse(value, dateTimeFormatter);
    }

    private static LocalTime toLocalTime(String value) {
        return LocalTime.parse(value, timeFormatter);
    }

    private static final List<String> POSITIVE_BOOLEAN_VALUES = Arrays.asList("Y", "YES", "TRUE");
    private static final List<String> NEGATIVE_BOOLEAN_VALUES = Arrays.asList("N", "NO", "FALSE");

    private static Boolean toBoolean(String value) {
        Boolean rtn = null;
        if (POSITIVE_BOOLEAN_VALUES.contains(value)) {
            return Boolean.TRUE;
        } else if (NEGATIVE_BOOLEAN_VALUES.contains(value)) {
            return Boolean.FALSE;
        }
        return rtn;
    }

}
