package uk.m0nom.adif3.transform;

import org.apache.commons.lang3.StringUtils;

public class CallsignUtils {
    private final static String[] portableSuffixes = new String[] {"/P", "/M", "/MM", "/PM"};

    public static boolean isNotFixed(String callsign) {
        return StringUtils.endsWithAny(callsign, portableSuffixes);
    }

    public CallsignSuffix getSuffix(String callsign) {
        for (CallsignSuffix toCheck : CallsignSuffix.values()) {
            if (StringUtils.endsWith(callsign, toCheck.name())) {
                return toCheck;
            }
        }
        return null;
    }
}
