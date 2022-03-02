package org.marsik.ham.adif;


import org.junit.jupiter.api.Test;

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
}
