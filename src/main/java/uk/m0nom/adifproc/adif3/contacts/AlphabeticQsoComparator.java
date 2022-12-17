package uk.m0nom.adifproc.adif3.contacts;

import java.util.Comparator;

public class AlphabeticQsoComparator implements Comparator<Qso> {
    @Override
    public int compare(Qso qso1, Qso qso2) {
        String callsign1 = qso1.getTo().getCallsign();
        String callsign2 = qso2.getTo().getCallsign();
        return callsign1.compareTo(callsign2);
    }
}
