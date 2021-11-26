package uk.m0nom.adif3.print;

/**
 * Utilities relating to printing
 */
public class PrintUtils {
    static String stripQuotes(String str) {
        String stripped = str;
        if (stripped.startsWith("'")) {
            stripped = stripped.substring(1, str.length());
        }
        if (stripped.endsWith("'")) {
            stripped = stripped.substring(0, stripped.length()-1);
        }
        return stripped;
    }
}
