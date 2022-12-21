package org.marsik.ham.adif;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.enums.*;
import org.marsik.ham.adif.types.Iota;
import org.marsik.ham.adif.types.PotaList;
import org.marsik.ham.adif.types.Sota;
import org.marsik.ham.adif.types.Wwff;
import org.marsik.ham.grid.CoordinateWriter;
import org.marsik.ham.util.MultiOptional;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.UnmappableCharacterException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.marsik.ham.adif.AdiWriter.*;

public class AdiReader {
    private static final Pattern NUMERIC_RE = Pattern.compile("-?\\d+(\\.\\d+)?");

    private boolean quirksMode = false;
    private String current;

    public boolean isQuirksMode() {
        return quirksMode;
    }

    public void setQuirksMode(boolean quirksMode) {
        this.quirksMode = quirksMode;
    }

    public Optional<org.marsik.ham.adif.Adif3> read(BufferedReader reader) throws IOException {
        org.marsik.ham.adif.Adif3 document = new Adif3();

        reader.mark(1);
        int c = reader.read();
        if (c == -1) {
            // EOF
            return Optional.empty();
        } else if (c != '<') {
            org.marsik.ham.adif.AdifHeader header = readHeader(reader);
            document.setHeader(header);
        } else {
            // No header
            reader.reset();
        }

        int recordCount = 1;
        while (true) {
            try {
                Map<String, String> recordFields = readRecord(reader);
                if (recordFields == null) {
                    break;
                }
                document.getRecords().add(parseRecord(recordFields));
                recordCount++;
            } catch (UnmappableCharacterException e) {
                System.err.printf("Caught unmappable character exception reading record number : %d, field: %s%n",
                        recordCount, current);
                throw e;
            } catch (RuntimeException e) {
                String msg = e.getMessage();
                throw new AdifReaderException(msg, recordCount, e);
            }
        }

        return Optional.of(document);
    }

    private org.marsik.ham.adif.Adif3Record parseRecord(Map<String, String> recordFields) {
        org.marsik.ham.adif.Adif3Record record = new Adif3Record();

        maybeGet(recordFields, "ADDRESS").ifPresent(record::setAddress);
        maybeGet(recordFields, "AGE").map(Integer::parseInt).ifPresent(record::setAge);
        maybeGet(recordFields, "A_INDEX").map(Double::parseDouble).ifPresent(record::setAIndex);
        maybeGet(recordFields, "ANT_AZ").map(Double::parseDouble).ifPresent(record::setAntAz);
        maybeGet(recordFields, "ANT_EL").map(Double::parseDouble).ifPresent(record::setAntEl);
        maybeGet(recordFields, "ANT_PATH").map(AntPath::findByCode).ifPresent(record::setAntPath);
        maybeGet(recordFields, "ARRL_SECT").ifPresent(record::setArrlSect);
        maybeGet(recordFields, "AWARD_SUBMITTED")
                .map(s -> parseCommaArray(s, String::valueOf))
                .ifPresent(record::setAwardSubmitted);
        maybeGet(recordFields, "AWARD_GRANTED")
                .map(s -> parseCommaArray(s, String::valueOf))
                .ifPresent(record::setAwardGranted);
        maybeGet(recordFields, "BAND").map(Band::findByCode).ifPresent(record::setBand);
        maybeGet(recordFields, "BAND_RX").map(Band::findByCode).ifPresent(record::setBandRx);
        maybeGet(recordFields, "CALL").ifPresent(record::setCall);
        maybeGet(recordFields, "CHECK").ifPresent(record::setCheck);
        maybeGet(recordFields, "CLASS").ifPresent(record::setContestClass);
        maybeGet(recordFields, "CLUBLOG_QSO_UPLOAD_DATE")
                .map(this::parseDate)
                .ifPresent(record::setClublogQsoUploadDate);
        maybeGet(recordFields, "CLUBLOG_QSO_UPLOAD_STATUS")
                .map(QsoUploadStatus::findByCode)
                .ifPresent(record::setClublogQsoUploadStatus);
        maybeGet(recordFields, "CNTY").ifPresent(record::setCnty);
        maybeGet(recordFields, "COMMENT").ifPresent(record::setComment);
        maybeGet(recordFields, "CONT").map(Continent::findByCode).ifPresent(record::setCont);
        maybeGet(recordFields, "CONTACTED_OP").ifPresent(record::setContactedOp);
        maybeGet(recordFields, "CONTEST_ID").ifPresent(record::setContestId);
        maybeGet(recordFields, "COUNTRY").ifPresent(record::setCountry);
        maybeGet(recordFields, "CQZ").map(Integer::parseInt).ifPresent(record::setCqz);
        maybeGet(recordFields, "CREDIT_SUBMITTED")
                .map(s -> parseCommaArray(s, String::valueOf))
                .ifPresent(record::setCreditSubmitted);
        maybeGet(recordFields, "CREDIT_GRANTED")
                .map(s -> parseCommaArray(s, String::valueOf))
                .ifPresent(record::setCreditGranted);
        maybeGet(recordFields, "DARC_DOK").ifPresent(record::setDarcDok);
        maybeGet(recordFields, "DISTANCE").map(Double::parseDouble).ifPresent(record::setDistance);
        maybeGet(recordFields, "DXCC").map(Integer::parseInt).ifPresent(record::setDxcc);
        maybeGet(recordFields, "EMAIL").ifPresent(record::setEmail);
        maybeGet(recordFields, "EQ_CALL").ifPresent(record::setEqCall);
        maybeGet(recordFields, "EQSL_QSLRDATE").map(this::parseDate).ifPresent(record::setEqslQslRDate);
        maybeGet(recordFields, "EQSL_QSLSDATE").map(this::parseDate).ifPresent(record::setEqslQslSDate);
        maybeGet(recordFields, "EQSL_QSL_RCVD").map(QslRcvd::findByCode).ifPresent(record::setEqslQslRcvd);
        maybeGet(recordFields, "EQSL_QSL_SENT").map(QslSent::findByCode).ifPresent(record::setEqslQslSent);
        maybeGet(recordFields, "FISTS").ifPresent(record::setFists);
        maybeGet(recordFields, "FISTS_CC").ifPresent(record::setFistsCc);
        maybeGet(recordFields, "FORCE_INT").map(this::parseBool).ifPresent(record::setForceInt);
        maybeGet(recordFields, "FREQ").filter(AdiReader::isNotEmpty).map(Double::parseDouble).ifPresent(record::setFreq);
        maybeGet(recordFields, "FREQ_RX").filter(AdiReader::isNotEmpty).map(Double::parseDouble).ifPresent(record::setFreqRx);
        maybeGet(recordFields, "GRIDSQUARE").ifPresent(record::setGridsquare);
        maybeGet(recordFields, "HRDLOG_QSO_UPLOAD_DATE")
                .map(this::parseDate)
                .ifPresent(record::setHrdlogQsoUploadDate);
        maybeGet(recordFields, "HRDLOG_QSO_UPLOAD_STATUS")
                .map(QsoUploadStatus::findByCode)
                .ifPresent(record::setHrdlogQsoUploadStatus);
        maybeGet(recordFields, "IOTA").map(Iota::findByCode).ifPresent(record::setIota);
        maybeGet(recordFields, "IOTA_ISLAND_ID").ifPresent(record::setIotaIslandId);
        maybeGet(recordFields, "ITUZ").map(Integer::parseInt).ifPresent(record::setItuz);
        maybeGet(recordFields, "K_INDEX").map(Double::parseDouble).ifPresent(record::setKIndex);

        Optional<Double> lat = maybeGet(recordFields, "LAT").map(CoordinateWriter::dmToLat);
        Optional<Double> lon = maybeGet(recordFields, "LON").map(CoordinateWriter::dmToLon);
        MultiOptional.two(lat, lon, GlobalCoordinates::new).ifPresent(record::setCoordinates);

        maybeGet(recordFields, "LOTW_QSLRDATE").map(this::parseDate).ifPresent(record::setLotwQslRDate);
        maybeGet(recordFields, "LOTW_QSLSDATE").map(this::parseDate).ifPresent(record::setLotwQslSDate);
        maybeGet(recordFields, "LOTW_QSL_RCVD").map(QslRcvd::findByCode).ifPresent(record::setLotwQslRcvd);
        maybeGet(recordFields, "LOTW_QSL_SENT").map(QslSent::findByCode).ifPresent(record::setLotwQslSent);
        maybeGet(recordFields, "MAX_BURSTS").map(Integer::parseInt).ifPresent(record::setMaxBursts);
        try {
            maybeGet(recordFields, "MODE").map(Mode::findByCode).ifPresent(record::setMode);
        } catch (IllegalArgumentException e) {
            if (quirksMode) {
                maybeGet(recordFields, "MODE").map(Submode::findByCode).ifPresent((sm) -> record.setMode(sm.getMode()));
            } else {
                throw e;
            }
        }
        maybeGet(recordFields, "MS_SHOWER").ifPresent(record::setMsShower);
        maybeGet(recordFields, "MY_ANTENNA").ifPresent(record::setMyAntenna);
        maybeGet(recordFields, "MY_CITY").ifPresent(record::setMyCity);
        maybeGet(recordFields, "MY_CNTY").ifPresent(record::setMyCnty);
        maybeGet(recordFields, "MY_COUNTRY").ifPresent(record::setMyCountry);
        maybeGet(recordFields, "MY_CQ_ZONE").map(Integer::parseInt).ifPresent(record::setMyCqZone);
        maybeGet(recordFields, "MY_DXCC").map(Integer::parseInt).ifPresent(record::setMyDxcc);
        maybeGet(recordFields, "MY_FISTS").ifPresent(record::setMyFists);
        maybeGet(recordFields, "MY_GRIDSQUARE").ifPresent(record::setMyGridSquare);
        maybeGet(recordFields, "MY_IOTA").map(Iota::findByCode).ifPresent(record::setMyIota);
        maybeGet(recordFields, "MY_IOTA_ISLAND_ID").ifPresent(record::setMyIotaIslandId);
        maybeGet(recordFields, "MY_ITU_ZONE").map(Integer::parseInt).ifPresent(record::setMyItuZone);

        Optional<Double> myLat = maybeGet(recordFields, "MY_LAT").map(CoordinateWriter::dmToLat);
        Optional<Double> myLon = maybeGet(recordFields, "MY_LON").map(CoordinateWriter::dmToLon);
        MultiOptional.two(myLat, myLon, GlobalCoordinates::new).ifPresent(record::setMyCoordinates);

        maybeGet(recordFields, "MY_NAME").ifPresent(record::setMyName);
        maybeGet(recordFields, "MY_POSTAL_CODE").ifPresent(record::setMyPostalCode);
        maybeGet(recordFields, "MY_RIG").ifPresent(record::setMyRig);
        maybeGet(recordFields, "MY_SIG").ifPresent(record::setMySig);
        maybeGet(recordFields, "MY_SIG_INFO").ifPresent(record::setMySigInfo);
        maybeGet(recordFields, "MY_SOTA_REF").map(Sota::valueOf).ifPresent(record::setMySotaRef);
        maybeGet(recordFields, "MY_STATE").ifPresent(record::setMyState);
        maybeGet(recordFields, "MY_STREET").ifPresent(record::setMyStreet);
        maybeGet(recordFields, "MY_USACA_COUNTIES")
                .map(s -> parseColonArray(s, String::valueOf))
                .ifPresent(record::setMyUsaCaCounties);
        maybeGet(recordFields, "MY_VUCC_GRIDS")
                .map(s -> parseCommaArray(s, String::valueOf))
                .ifPresent(record::setMyVuccGrids);
        maybeGet(recordFields, "NAME").ifPresent(record::setName);
        maybeGet(recordFields, "NOTES").ifPresent(record::setNotes);
        maybeGet(recordFields, "NR_BURSTS").map(Integer::parseInt).ifPresent(record::setNrBursts);
        maybeGet(recordFields, "NR_PINGS").map(Integer::parseInt).ifPresent(record::setNrPings);
        maybeGet(recordFields, "OPERATOR").ifPresent(record::setOperator);
        maybeGet(recordFields, "OWNER_CALLSIGN").ifPresent(record::setOwnerCallsign);
        maybeGet(recordFields, "PFX").ifPresent(record::setPfx);
        maybeGet(recordFields, "PRECEDENCE").ifPresent(record::setPrecedence);
        maybeGet(recordFields, "PROP_MODE").map(Propagation::findByCode).ifPresent(record::setPropMode);
        maybeGet(recordFields, "PUBLIC_KEY").ifPresent(record::setPublicKey);
        maybeGet(recordFields, "QRZCOM_QSO_UPLOAD_DATE")
                .map(this::parseDate)
                .ifPresent(record::setQrzcomQsoUploadDate);
        maybeGet(recordFields, "QRZCOM_QSO_UPLOAD_STATUS")
                .map(QsoUploadStatus::findByCode)
                .ifPresent(record::setQrzcomQsoUploadStatus);
        maybeGet(recordFields, "QSLMSG").ifPresent(record::setQslMsg);
        maybeGet(recordFields, "QSLRDATE").map(this::parseLocalDate).ifPresent(record::setQslRDate);
        maybeGet(recordFields, "QSLSDATE").map(this::parseLocalDate).ifPresent(record::setQslSDate);
        maybeGet(recordFields, "QSL_RCVD").map(QslRcvd::findByCode).ifPresent(record::setQslRcvd);
        maybeGet(recordFields, "QSL_RCVD_VIA").map(QslVia::findByCode).ifPresent(record::setQslRcvdVia);
        maybeGet(recordFields, "QSL_SENT").map(QslSent::findByCode).ifPresent(record::setQslSent);
        maybeGet(recordFields, "QSL_SENT_VIA").map(QslVia::findByCode).ifPresent(record::setQslSentVia);
        maybeGet(recordFields, "QSL_VIA").ifPresent(record::setQslVia);
        maybeGet(recordFields, "QSO_COMPLETE").map(QsoComplete::findByCode).ifPresent(record::setQsoComplete);
        maybeGet(recordFields, "QSO_DATE").map(s -> LocalDate.parse(s, dateFormatter)).ifPresent(record::setQsoDate);
        maybeGet(recordFields, "QSO_DATE_OFF").map(s -> LocalDate.parse(s, dateFormatter)).ifPresent(record::setQsoDateOff);
        maybeGet(recordFields, "QSO_RANDOM").map(this::parseBool).ifPresent(record::setQsoRandom);
        maybeGet(recordFields, "QTH").ifPresent(record::setQth);
        maybeGet(recordFields, "REGION").ifPresent(record::setRegion);
        maybeGet(recordFields, "RIG").ifPresent(record::setRig);
        maybeGet(recordFields, "RST_RCVD").ifPresent(record::setRstRcvd);
        maybeGet(recordFields, "RST_SENT").ifPresent(record::setRstSent);
        maybeGet(recordFields, "RX_PWR")
                .map(s -> s.replaceAll("[wW]$", ""))
                .filter(AdiReader::isNumeric)
                .map(Double::parseDouble)
                .ifPresent(record::setRxPwr);
        maybeGet(recordFields, "SAT_MODE").ifPresent(record::setSatMode);
        maybeGet(recordFields, "SAT_NAME").ifPresent(record::setSatName);
        maybeGet(recordFields, "SFI").map(Double::parseDouble).ifPresent(record::setSfi);
        maybeGet(recordFields, "SIG").ifPresent(record::setSig);
        maybeGet(recordFields, "SIG_INFO").ifPresent(record::setSigInfo);
        maybeGet(recordFields, "SILENT_KEY").map(this::parseBool).ifPresent(record::setSilentKey);
        maybeGet(recordFields, "SKCC").ifPresent(record::setSkcc);
        maybeGet(recordFields, "SOTA_REF").map(Sota::valueOf).ifPresent(record::setSotaRef);
        maybeGet(recordFields, "SRX").map(Integer::parseInt).ifPresent(record::setSrx);
        maybeGet(recordFields, "SRX_STRING").ifPresent(record::setSrxString);
        maybeGet(recordFields, "STATE").ifPresent(record::setState);
        maybeGet(recordFields, "STATION_CALLSIGN").ifPresent(record::setStationCallsign);
        maybeGet(recordFields, "STX").map(Integer::parseInt).ifPresent(record::setStx);
        maybeGet(recordFields, "STX_STRING").ifPresent(record::setStxString);
        maybeGet(recordFields, "SUBMODE").ifPresent(record::setSubmode);
        maybeGet(recordFields, "SWL").map(this::parseBool).ifPresent(record::setSwl);
        maybeGet(recordFields, "TEN_TEN").map(Integer::parseInt).ifPresent(record::setTenTen);
        maybeGet(recordFields, "TIME_OFF").map(this::parseTime).ifPresent(record::setTimeOff);
        maybeGet(recordFields, "TIME_ON").map(this::parseTime).ifPresent(record::setTimeOn);
        maybeGet(recordFields, "TX_PWR")
                .map(s -> s.replaceAll("[wW]$", ""))
                .filter(AdiReader::isNumeric)
                .map(Double::parseDouble)
                .ifPresent(record::setTxPwr);
        maybeGet(recordFields, "UKSMG").map(Integer::parseInt).ifPresent(record::setUksmg);
        maybeGet(recordFields, "USACA_COUNTIES")
                .map(s -> parseColonArray(s, String::valueOf))
                .ifPresent(record::setUsaCaCounties);
        maybeGet(recordFields, "VUCC_GRIDS")
                .map(s -> parseCommaArray(s, String::valueOf))
                .ifPresent(record::setVuccGrids);
        maybeGet(recordFields, "WEB").ifPresent(record::setWeb);

        maybeGetCustomDefinedFields("APP_", recordFields, record.getApplicationDefinedFields());
        maybeGetCustomDefinedFields("USER_", recordFields, record.getUserDefinedFields());
        
        /* ADIF 3.1.3 fields */
        maybeGet(recordFields, "MY_ARRL_SECT").ifPresent(record::setMyArrlSect);

        maybeGet(recordFields, "MY_WWFF_REF").map(Wwff::valueOf).ifPresent(record::setMyWwffRef);
        maybeGet(recordFields, "WWFF_REF").map(Wwff::valueOf).ifPresent(record::setWwffRef);

        /* ADIF 3.1.4 fields */
        maybeGet(recordFields, "ALTITUDE").map(Double::parseDouble).ifPresent(record::setAltitude);
        maybeGet(recordFields, "MY_ALTITUDE").map(Double::parseDouble).ifPresent(record::setMyAltitude);
        maybeGet(recordFields, "GRIDSQUARE_EXT").ifPresent(record::setGridsquareExt);
        maybeGet(recordFields, "MY_GRIDSQUARE_EXT").ifPresent(record::setMyGridsquareExt);
        maybeGet(recordFields, "MY_POTA_REF").map(PotaList::valueOf).ifPresent(record::setMyPotaRef);
        maybeGet(recordFields, "POTA_REF").map(PotaList::valueOf).ifPresent(record::setPotaRef);

        maybeGet(recordFields, "HAMLOGEU_QSO_UPLOAD_DATE")
                .map(this::parseDate)
                .ifPresent(record::setHamlogEuQsoUploadDate);
        maybeGet(recordFields, "HAMLOGEU_QSO_UPLOAD_STATUS")
                .map(QsoUploadStatus::findByCode)
                .ifPresent(record::setHamlogEuQsoUploadStatus);
        maybeGet(recordFields, "HAMQTH_QSO_UPLOAD_DATE")
                .map(this::parseDate)
                .ifPresent(record::setHamqthQsoUploadDate);
        maybeGet(recordFields, "HAMQTH_QSO_UPLOAD_STATUS")
                .map(QsoUploadStatus::findByCode)
                .ifPresent(record::setHamqthQsoUploadStatus);

        return record;
    }

    private void maybeGetCustomDefinedFields(String prefix, Map<String, String> recordFields, Map<String, String> customFieldMap) {
        Set<String> appDefinedFields = recordFields.keySet().stream().filter(s -> s.startsWith(prefix)).collect(Collectors.toSet());

        for (String appDefinedField : appDefinedFields) {
            customFieldMap.put(appDefinedField, recordFields.get(appDefinedField));
        }
    }

    private <K, V> Optional<V> maybeGet(Map<K, V> map, K key) {
        current = key.toString();
        V res = map.get(key);
        return res == null ? Optional.empty() : Optional.of(res);
    }

    Map<String, String> readRecord(BufferedReader reader) throws IOException {
        Map<String, String> fields = new HashMap<>();
        Field field = readField(reader);
        if (field == null) {
            return null;
        }

        while (field != null && !"eor".equalsIgnoreCase(field.getName())) {
            fields.put(field.getName().toUpperCase(), field.getValue());
            field = readField(reader);
        }

        return fields;
    }

    @Data
    @AllArgsConstructor
    static class Field {
        String name;
        String value;
    }

    @Data
    @AllArgsConstructor
    static class Tag {
        String name;
        int length;
        String type;
    }

    private Tag parseTag(String tag) {
        String[] pieces = tag.substring(1, tag.length() - 1).split(":");
        return new Tag(pieces.length > 0 ? pieces[0] : null,
                pieces.length > 1 ? Integer.parseInt(pieces[1]) : 0,
                pieces.length > 2 ? pieces[2] : null);
    }

    Field readField(BufferedReader reader) throws IOException {
        readUntil(reader, '<', false);
        final String tag = readUntil(reader, '>', true);
        if (tag.isEmpty()) {
            return null;
        }

        final Tag parsedTag = parseTag(tag);
        if (parsedTag.getLength() == 0) {
            return new Field(parsedTag.getName(), "");
        }

        final int len = parsedTag.getLength();
        final String value = readLength(reader, len);

        return new Field(parsedTag.getName(), value.trim());
    }

    private String readLength(BufferedReader reader, int len) throws IOException {
        char[] content = new char[len]; // todo limit length to avoid DOS attack
        int res;
        int read = 0;
        while (read < len) {
            res = reader.read(content, read, len - read);
            if (res >= 0) {
                read += res;
            } else {
                //end-of-file sanity check
                read = len;
            }
        }
        return String.copyValueOf(content);
    }

    org.marsik.ham.adif.AdifHeader readHeader(BufferedReader reader) throws IOException {
        org.marsik.ham.adif.AdifHeader header = new AdifHeader();

        // read all content until <eoh> (case insensitive)
        while (true) {
            readUntil(reader, '<', false);
            String tag = readUntil(reader, '>', true);
            Tag parsedTag = parseTag(tag);
            final String value = readLength(reader, parsedTag.getLength());

            if ("eoh".equalsIgnoreCase(parsedTag.getName())) {
                break;
            } else if ("adif_ver".equalsIgnoreCase(parsedTag.getName())) {
                header.setVersion(value);
            } else if ("programid".equalsIgnoreCase(parsedTag.getName())) {
                header.setProgramId(value);
            } else if ("programversion".equalsIgnoreCase(parsedTag.getName())) {
                header.setProgramVersion(value);
            } else if ("created_timestamp".equalsIgnoreCase(parsedTag.getName())) {
                header.setTimestamp(
                        LocalDateTime.parse(value, DateTimeFormatter.ofPattern("yyyyMMdd HHmmss")).atZone(ZoneId.of("UTC")));
            }
        }

        return header;
    }

    private String readUntil(Reader reader, char stop, boolean inclusive) throws IOException {
        reader.mark(1);
        int c = reader.read();
        StringBuilder builder = new StringBuilder();
        while (c != -1 && c != stop) {
            builder.append((char) c);
            reader.mark(1);
            c = reader.read();
        }

        if (c != -1 && inclusive) {
            builder.append((char) c);
        } else {
            reader.reset();
        }

        return builder.toString();
    }

    private ZonedDateTime parseDate(String s) {
        return LocalDate.parse(s, dateFormatter).atStartOfDay(ZoneId.of("UTC"));
    }

    private LocalDate parseLocalDate(String s) {
        return LocalDate.parse(s, dateFormatter);
    }

    private LocalTime parseTime(String s) {
        return LocalTime.parse(s,
                s.length() > 4 ? timeFormatter : timeFormatterShort);
    }

    private boolean parseBool(String s) {
        return s.equalsIgnoreCase("Y");
    }

    private <T> List<T> parseCommaArray(String s, Function<String, T> fieldConverter) {
        return Stream.of(s.split(","))
                .map(fieldConverter)
                .collect(Collectors.toList());
    }

    private static boolean isNumeric(String s)
    {
        return NUMERIC_RE.matcher(s).matches();
    }

    private static boolean isNotEmpty(String s)
    {
        return s != null && s.length() > 0;
    }

    private <T> List<T> parseColonArray(String s, Function<String, T> fieldConverter) {
        return Stream.of(s.split(":"))
                .map(fieldConverter)
                .collect(Collectors.toList());
    }
}
