package uk.m0nom.adif3.transform;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class CallsignUtilsTest {
    @Test
    public void testForMobile() {
        assertTrue(CallsignUtils.isPortable("M0NOM/M"));
        assertTrue(CallsignUtils.isPortable("M0NOM/PM"));
        assertTrue(CallsignUtils.isPortable("M0NOM/MM"));
        assertTrue(CallsignUtils.isPortable("M0NOM/P"));
        assertTrue(CallsignUtils.isPortable("LZ/M0NOM/P"));
    }
}
