package org.marsik.ham.adif;


import org.junit.jupiter.api.Test;
import org.marsik.ham.adif.enums.ArrlSection;
import org.marsik.ham.adif.types.Wwff;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;

public class AdiWriterTest {
    @Test
    public void testBool() {
        org.marsik.ham.adif.AdiWriter writer = new org.marsik.ham.adif.AdiWriter();
        writer.append("TRUE", true);
        writer.append("FALSE", false);

        assertThat(writer.toString())
                .isEqualTo("<TRUE:1>Y<FALSE:1>N");
    }

    @Test
    public void testSimpleQso() {
        org.marsik.ham.adif.AdiWriter writer = new org.marsik.ham.adif.AdiWriter();

        org.marsik.ham.adif.Adif3Record record = new org.marsik.ham.adif.Adif3Record();
        record.setCall("OK7MS/p");
        writer.append(record);

        assertThat(writer.toString())
                .isEqualTo("<CALL:7>OK7MS/p<EOR>\n");
    }

    @Test
    public void testSimpleQsoWithTime() {
        org.marsik.ham.adif.AdiWriter writer = new AdiWriter();

        org.marsik.ham.adif.Adif3Record record = new Adif3Record();
        record.setCall("OK7MS/p");
        record.setTimeOn(LocalTime.of(15, 30));
        writer.append(record);

        assertThat(writer.toString())
                .isEqualTo("<CALL:7>OK7MS/p<TIME_ON:6>153000<EOR>\n");
    }


    @Test
    public void testSimpleQsoWithAppCustomField() {
        org.marsik.ham.adif.AdiWriter writer = new org.marsik.ham.adif.AdiWriter();

        org.marsik.ham.adif.Adif3Record record = new org.marsik.ham.adif.Adif3Record();
        record.addApplicationDefinedField("APP_APROC_ALT", "9000");
        writer.append(record);

        assertThat(writer.toString())
                .isEqualTo("<APP_APROC_ALT:4>9000<EOR>\n");
    }


    @Test
    public void testSimpleQsoWithUserCustomField() {
        org.marsik.ham.adif.AdiWriter writer = new org.marsik.ham.adif.AdiWriter();

        org.marsik.ham.adif.Adif3Record record = new org.marsik.ham.adif.Adif3Record();
        record.addUserDefinedField("USER_ANT", "Dipole");
        writer.append(record);

        assertThat(writer.toString())
                .isEqualTo("<USER_ANT:6>Dipole<EOR>\n");
    }
    
    @Test
    public void test313WwffReferences() {
        AdiWriter writer = new AdiWriter();

        Adif3Record record = new Adif3Record();
        record.setCall("OK7MS/p");
        record.setTimeOn(LocalTime.of(15, 30));
        record.setMyWwffRef(Wwff.valueOf("GFF-0350"));
        record.setWwffRef(Wwff.valueOf("S9FF-0001"));
        writer.append(record);

        assertThat(writer.toString())
                .isEqualTo("<CALL:7>OK7MS/p<TIME_ON:6>153000<MY_WWFF_REF:8>GFF-0350<WWFF_REF:9>S9FF-0001<EOR>\n");
    }

    @Test
    public void testArrlSect() {
        AdiWriter writer = new AdiWriter();

        Adif3Record record = new Adif3Record();
        record.setCall("OK7MS/p");
        record.setTimeOn(LocalTime.of(15, 30));
        record.setArrlSect(ArrlSection.CT.adifCode());
        record.setMyArrlSect(ArrlSection.AZ.adifCode());
        writer.append(record);

        assertThat(writer.toString())
                .isEqualTo("<ARRL_SECT:2>CT<CALL:7>OK7MS/p<TIME_ON:6>153000<MY_ARRL_SECT:2>AZ<EOR>\n");

    }
}
