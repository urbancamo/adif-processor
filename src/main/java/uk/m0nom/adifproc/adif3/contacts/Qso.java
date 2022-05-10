package uk.m0nom.adifproc.adif3.contacts;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.marsik.ham.adif.Adif3Record;

/**
 * Captures the information relating to a single contact. Each station in the QSO is recorded as a single
 * instance of Station which allows some more OO-centric processing compared to the raw data in the Adif3Record
 */
@Data
@NoArgsConstructor
public class Qso {
    private int index;
    private Station from;
    private Station to;
    private Adif3Record record;

    public Qso(Adif3Record rec, int index) {
        setIndex(index);
        setRecord(rec);
    }

    public boolean doingSameActivity() {
       return from.doingSameActivityAs(to);
    }

    public boolean isSatelliteContact() {
        return (record != null) && (record.getSatName() != null);
    }
}
