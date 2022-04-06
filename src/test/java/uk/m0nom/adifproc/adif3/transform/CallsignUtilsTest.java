package uk.m0nom.adifproc.adif3.transform;


import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CallsignUtilsTest {
    @Test
    public void testForMobile() {
        assertThat(CallsignUtils.isPortable("M0NOM")).isFalse();
        assertThat(CallsignUtils.isPortable("M0NOM/PM")).isTrue();
        assertThat(CallsignUtils.isPortable("M0NOM/MM")).isTrue();
        assertThat(CallsignUtils.isPortable("M0NOM/P")).isTrue();
        assertThat(CallsignUtils.isPortable("LZ/M0NOM/P")).isTrue();
    }
}
