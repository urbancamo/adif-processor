package uk.m0nom.adif3.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CallsignUtilsTest {
    @Test
    public void testForMobile() {
        assertTrue(CallsignUtils.isNotFixed("M0NOM/M"));
        assertTrue(CallsignUtils.isNotFixed("M0NOM/PM"));
        assertTrue(CallsignUtils.isNotFixed("M0NOM/MM"));
        assertTrue(CallsignUtils.isNotFixed("M0NOM/P"));
        assertTrue(CallsignUtils.isNotFixed("LZ/M0NOM/P"));
    }
}
