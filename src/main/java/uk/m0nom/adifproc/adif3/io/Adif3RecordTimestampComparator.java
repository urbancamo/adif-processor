package uk.m0nom.adifproc.adif3.io;

import org.marsik.ham.adif.Adif3Record;

import java.util.Comparator;

/**
 * Compare two QSOs based on their date, if date is the same then the time on.
 * This allows the QSO list to be sorted chronologically.
 */
class Adif3RecordTimestampComparator implements Comparator<Adif3Record> {
    @Override
    public int compare(Adif3Record o1, Adif3Record o2) {
        int dateCompare = o1.getQsoDate().compareTo(o2.getQsoDate());
        int timeCompare = o1.getTimeOn().compareTo(o2.getTimeOn());
        if (dateCompare != 0) {
            return dateCompare;
        }
        if (timeCompare != 0) {
            return timeCompare;
        } else {
            return o1.getCall().compareTo(o2.getCall());
        }

    }
}
