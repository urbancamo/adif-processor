package uk.m0nom.adif3.contacts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.ham.adif.Adif3Record;

/**
 * Captures the information relating to a single contact. Each station in the QSO is recorded as a single
 * instance of Station which allows some more OO-centric processing compared ot the raw data in the Adif3Record
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Qso {
    private int index;
    private Station from;
    private Station to;
    private Adif3Record record;

    public boolean doingSameActivity() {
       return from.doingSameActivityAs(to);
    }

    public boolean isSatelliteContact() {
        return (record != null) && (record.getSatName() != null);
    }
}
