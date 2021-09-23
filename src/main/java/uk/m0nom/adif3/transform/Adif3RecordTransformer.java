package uk.m0nom.adif3.transform;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.contacts.Qsos;

public interface Adif3RecordTransformer {
    void transform(Qsos qsos, Adif3Record rec, int index);
}
