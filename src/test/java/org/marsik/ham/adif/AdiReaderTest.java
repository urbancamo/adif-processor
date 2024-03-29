package org.marsik.ham.adif;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.marsik.ham.adif.enums.ArrlSection;
import org.marsik.ham.adif.types.Pota;
import org.marsik.ham.adif.types.PotaList;
import org.marsik.ham.adif.types.Wwff;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.time.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.marsik.ham.adif.enums.Band.*;
import static org.marsik.ham.adif.enums.Mode.*;
import static org.marsik.ham.adif.enums.Submode.JT9H_FAST;
import static org.marsik.ham.adif.enums.Submode.PSK63;


public class AdiReaderTest {
    @Test
    public void testReadField() throws Exception {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader = mockInput("   <test:3>abcde");
        AdiReader.Field f = reader.readField(inputReader);

        assertThat(f.getName())
                .isEqualTo("test");

        assertThat(f.getValue())
                .isEqualTo("abc");
    }

    @Test
    public void testReadFieldLengthTooLarge() throws Exception {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader = mockInput("   <test:6>abcde<");
        AdiReader.Field f = reader.readField(inputReader);

        assertThat(f.getName())
                .isEqualTo("test");

        assertThat(f.getValue())
                .isEqualTo("abcde<");
    }

    @Test
    public void testReadFieldTrimmmed() throws Exception {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader = mockInput("   <test:6>abcde <");
        AdiReader.Field f = reader.readField(inputReader);

        assertThat(f.getName())
                .isEqualTo("test");

        assertThat(f.getValue())
                .isEqualTo("abcde");
    }

    @Test
    public void testReadRecord() throws Exception {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader = mockInput("   <test:3>abcde<lala:0>asd<lili:2>ab<eOr>");
        Map<String,String> fields = reader.readRecord(inputReader);

        assertThat(fields)
                .isNotNull()
                .hasSize(3)
                .containsKeys("TEST", "LALA", "LILI");

        assertThat(fields.get("TEST"))
                .isEqualTo("abc");
        assertThat(fields.get("LALA"))
                .isEqualTo("");
        assertThat(fields.get("LILI"))
                .isEqualTo("ab");
    }

    @Test
    public void testReadMultilineRecord() throws Exception {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader = mockInput("   <test:7>abc\r\nde<lala:3>asd");
        Map<String,String> fields = reader.readRecord(inputReader);

        assertThat(fields)
                .isNotNull()
                .hasSize(2)
                .containsKeys("TEST", "LALA");

        assertThat(fields.get("TEST"))
                .isEqualTo("abc\r\nde");
        assertThat(fields.get("LALA"))
                .isEqualTo("asd");
    }

    @Test
    public void testReadRecordAfterHeader() throws Exception {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader = mockInput(" #treafa<ehc:4>sfar<eoh>  <test:3>abcde<lala:0>asd<lili:2>ab<eOr>");
        reader.readHeader(inputReader);
        Map<String,String> fields = reader.readRecord(inputReader);

        assertThat(fields)
                .isNotNull()
                .hasSize(3)
                .containsKeys("TEST", "LALA", "LILI");
    }

    @Test
    public void testReadHeader() throws Exception {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader = mockInput(" #treafa<created_timestamp:15>20170216 224815<eoH>");
        AdifHeader header = reader.readHeader(inputReader);

        assertThat(header)
                .isNotNull();

        assertThat(header.getTimestamp())
                .isEqualTo(ZonedDateTime.of(2017, 2, 16, 22, 48, 15, 0, ZoneId.of("UTC")));
    }

    @Test
    public void testMultipleRecords() throws Exception {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader = mockInput("afad<eoh><eor><eor>");
        reader.read(inputReader);
    }

    private final static ZonedDateTime firstExpectedDate = LocalDate.of(1990, 6, 20).atStartOfDay(ZoneOffset.UTC);
    private final static ZonedDateTime secondExpectedDate = LocalDate.of(2010, 10, 22).atStartOfDay(ZoneOffset.UTC);
    private final static ZonedDateTime thirdExpectedDate = LocalDate.of(2018, 10, 16).atStartOfDay(ZoneOffset.UTC);
    @Test
    public void testAdifSample() throws Exception {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader = resourceInput("adif/sample.adi");
        Optional<Adif3> adif = reader.read(inputReader);
        assertThat(adif.get().header.version).isEqualTo("3.1.4");
        assertThat(adif.get().header.programId).isEqualTo("monolog");
        assertThat(adif.get().records)
                .isNotNull()
                .hasSize(4);

        assertThat(adif.get().records.get(0).getQsoDate()).isEqualTo(firstExpectedDate);
        assertThat(adif.get().records.get(0).getTimeOn()).isEqualTo(LocalTime.of(15, 23));
        assertThat(adif.get().records.get(0).getCall()).isEqualTo("VK9NS");
        assertThat(adif.get().records.get(0).getBand()).isEqualTo(BAND_20m);
        assertThat(adif.get().records.get(0).getMode()).isEqualTo(RTTY);
        assertThat(adif.get().records.get(0).getTxPwr()).isEqualTo(10.0);
        assertThat(adif.get().records.get(0).getApplicationDefinedField("APP_APROC_ALT")).isEqualTo("9000");

        assertThat(adif.get().records.get(1).getQsoDate()).isEqualTo(secondExpectedDate);
        assertThat(adif.get().records.get(1).getTimeOn()).isEqualTo(LocalTime.of(1, 11));
        assertThat(adif.get().records.get(1).getCall()).isEqualTo("ON4UN");
        assertThat(adif.get().records.get(1).getBand()).isEqualTo(BAND_40m);
        assertThat(adif.get().records.get(1).getMode()).isEqualTo(PSK);

        String ls = System.getProperty("line.separator");
        String address = String.format("John Doe%s100 Main Street%sCity, ST 12345", ls, ls);
        if (ls.length() == 2) {
            // Compensate for the fact that on Windows machines the line separator is 2
            // characters - and these are counted in the field length. So compared to a
            // unix delimited file the string is two characters less.
            address = address.substring(0, address.length()-2);
        }
        assertThat(adif.get().records.get(1).getAddress()).isEqualTo(address);
        assertThat(adif.get().records.get(1).getSilentKey()).isEqualTo(true);
        assertThat(adif.get().records.get(1).getSubmode()).isEqualTo(PSK63.adifCode());
        assertThat(adif.get().records.get(1).getTxPwr()).isEqualTo(2.0);
        assertThat(adif.get().records.get(1).getUserDefinedField("USER_ANT")).isEqualTo("Dipole");

        assertThat(adif.get().records.get(2).getQsoDate()).isEqualTo(thirdExpectedDate);
        assertThat(adif.get().records.get(2).getTimeOn()).isEqualTo(LocalTime.of(23, 15));
        assertThat(adif.get().records.get(2).getCall()).isEqualTo("K0TET");
        assertThat(adif.get().records.get(2).getBand()).isEqualTo(BAND_70cm);
        assertThat(adif.get().records.get(2).getMode()).isEqualTo(JT9);
        assertThat(adif.get().records.get(2).getSubmode()).isEqualTo(JT9H_FAST.adifCode());
        assertThat(adif.get().records.get(2).getTxPwr()).isEqualTo(100.0);
        assertThat(adif.get().records.get(2).getMyWwffRef()).isEqualTo(Wwff.valueOf("GFF-0350"));
        assertThat(adif.get().records.get(2).getWwffRef()).isEqualTo(Wwff.valueOf("S9FF-0001"));
        assertThat(adif.get().records.get(2).getArrlSect()).isEqualTo(ArrlSection.CT.adifCode());
        assertThat(adif.get().records.get(2).getMyArrlSect()).isEqualTo(ArrlSection.AZ.adifCode());

        Adif3Record rec3 = adif.get().records.get(3);
        assertThat(rec3.getQsoDate()).isEqualTo(thirdExpectedDate);
        assertThat(rec3.getTimeOn()).isEqualTo(LocalTime.of(23, 18));
        assertThat(rec3.getCall()).isEqualTo("K0ABC");
        assertThat(rec3.getBand()).isEqualTo(BAND_20m);
        assertThat(rec3.getAltitude()).isEqualTo(1234.0);
        assertThat(rec3.getMyAltitude()).isEqualTo(123.0);
        checkPotas(rec3.getMyPotaRef());
        checkPotas(rec3.getPotaRef());
    }

    // K-0817,K-4566,K-4576,K-4573,K-4578@US-WY
    private void checkPotas(PotaList potaList) {
        List<Pota> potas = potaList.getPotaList();
        assertThat(potas.size()).isEqualTo(5);
        assertThat(potas.get(0).getValue()).isEqualTo("K-0817");
        assertThat(potas.get(1).getValue()).isEqualTo("K-4566");
        assertThat(potas.get(2).getValue()).isEqualTo("K-4576");
        assertThat(potas.get(3).getValue()).isEqualTo("K-4573");
        assertThat(potas.get(4).getValue()).isEqualTo("K-4578@US-WY");

    }

    @Test
    public void testAdifSampleHandleUnsupportedCharacter() throws Exception {
        AdiReader reader = new AdiReader();
        BufferedReader inputReader = resourceInput("adif/m0nom_p.204681.20210116183312.adi");
        reader.read(inputReader);
    }

    private BufferedReader mockInput(String input) {
        return new BufferedReader(new StringReader(input));
    }

    private BufferedReader resourceInput(String path) {
        return new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream(path)));
    }
}
