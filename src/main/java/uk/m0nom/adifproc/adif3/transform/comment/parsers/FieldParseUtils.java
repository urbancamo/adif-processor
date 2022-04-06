package uk.m0nom.adifproc.adif3.transform.comment.parsers;

import org.apache.commons.lang3.StringUtils;

public class FieldParseUtils {

    /**
     * Process a power string into a double
     */
    public static double parsePwr(String value) throws NumberFormatException {
        String pwr = value.toLowerCase().trim();
        if (pwr.endsWith("w")) {
            pwr = StringUtils.replace(pwr, "w", "");
        } else if (pwr.endsWith(" w")) {
            pwr = StringUtils.replace(pwr, " w", "");
        } else if (pwr.endsWith(" watt")) {
            pwr = StringUtils.replace(pwr, " watt", "");
        } else if (pwr.endsWith(" watts")) {
            pwr = StringUtils.replace(pwr, " watts", "");
        }
        if (pwr.endsWith("k")) {
            pwr = StringUtils.replace(pwr, "k", "000");
        }
        return Double.parseDouble(pwr);
    }}
