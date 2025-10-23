package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Strings;

public class FieldParseUtils {

    /**
     * Process a power string into a double
     */
    public static double parsePwr(String value) throws NumberFormatException {
        String pwr = value.toLowerCase().trim();
        if (pwr.endsWith("w")) {
            pwr = Strings.CI.replace(pwr, "w", "");
        } else if (pwr.endsWith(" w")) {
            pwr = Strings.CI.replace(pwr, " w", "");
        } else if (pwr.endsWith(" watt")) {
            pwr = Strings.CI.replace(pwr, " watt", "");
        } else if (pwr.endsWith(" watts")) {
            pwr = Strings.CI.replace(pwr, " watts", "");
        }
        if (pwr.endsWith("k")) {
            pwr = Strings.CI.replace(pwr, "k", "000");
        }
        return Double.parseDouble(pwr);
    }

    public static double parseAlt(String value) throws NumberFormatException {
        if (value == null) {
            return 0.0;
        }

        String alt = value.toLowerCase().trim();
        double multiplier = 1;

        if (alt.endsWith("ft")) {
            multiplier = 1.0/3.28084;
            alt = Strings.CI.replace(alt, "ft", "").trim();
        } else if (alt.endsWith("m")) {
            alt = Strings.CI.replace(alt, "m", "").trim();
        }
        return Double.parseDouble(alt) * multiplier;
    }
}
