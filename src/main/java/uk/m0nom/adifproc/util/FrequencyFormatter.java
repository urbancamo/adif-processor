package uk.m0nom.adifproc.util;

public class FrequencyFormatter {
    /**
     * Format Frequency
     * @param freqInMhz input frequency in Mhz
     * @return formatted to Hz, KHz, MHz or GHz
     */
    public static String formatFrequency(Double freqInMhz) {
        String fmt = "";
        if (freqInMhz != null) {
            double freq = freqInMhz * 1000000.0;
            if (freq < 1E3) {
                fmt = String.format("%,.0f %s", freq, "Hz");
            }
            else if (freq < 1E6) {
                fmt = String.format("%,.0f %s", freq / 1E3, "KHz");
            }
            else if (freq < 1E9) {
                fmt = String.format("%,.3f %s", freqInMhz, "MHz");
            } else {
                fmt = String.format("%,.6f %s", freqInMhz / 1E3, "GHz");
            }
        }
        return fmt;
    }
}
