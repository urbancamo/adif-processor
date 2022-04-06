package uk.m0nom.adifproc.adif3.transform;

import org.apache.commons.lang3.StringUtils;

/**
 * Utility methods relating to callsigns
 */
public class CallsignUtils {

    public static boolean isPortable(String callsign) {
        for (CallsignSuffix suffix : CallsignSuffix.values()) {
            if (StringUtils.endsWithAny(callsign, suffix.getSuffix())) {
                return suffix.isPortable();
            }
        }
        return false;
    }
}
