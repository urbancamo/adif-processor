package uk.m0nom.adifproc.util;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

public class FrequencyFormatterTest {
    @Test
    public void testHz() {
        assertThat(FrequencyFormatter.formatFrequency(1E-6)).isEqualTo("1 Hz");
    }

    @Test
    public void testHzBoundary() {
        assertThat(FrequencyFormatter.formatFrequency(999 / 1E6)).isEqualTo("999 Hz");
    }


    @Test
    public void testKHz() {
        assertThat(FrequencyFormatter.formatFrequency(1E-3)).isEqualTo("1 KHz");
    }

    @Test
    public void testKHzBoundary() {
        assertThat(FrequencyFormatter.formatFrequency(999 / 1E3)).isEqualTo("999 KHz");
    }


    @Test
    public void testMHz() {
        assertThat(FrequencyFormatter.formatFrequency(145.500)).isEqualTo("145.500 MHz");
    }

    @Test
    public void testMHzBoundary() {
        assertThat(FrequencyFormatter.formatFrequency(999.0)).isEqualTo("999.000 MHz");
    }


    @Test
    public void testGHz() {
        assertThat(FrequencyFormatter.formatFrequency(1241.750)).isEqualTo("1.241750 GHz");
    }
}
