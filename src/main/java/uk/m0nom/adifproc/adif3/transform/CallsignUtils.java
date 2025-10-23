package uk.m0nom.adifproc.adif3.transform;

import org.apache.commons.lang3.Strings;

/**
 * Utility methods relating to callsigns
 */
public class CallsignUtils {

    public static boolean isPortable(String callsign) {
        for (CallsignSuffix suffix : CallsignSuffix.values()) {
            if (Strings.CI.endsWithAny(callsign, suffix.getSuffix())) {
                return suffix.isPortable();
            }
        }
        return false;
    }
}
