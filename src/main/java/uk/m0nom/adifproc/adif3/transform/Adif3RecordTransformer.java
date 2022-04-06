package uk.m0nom.adifproc.adif3.transform;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.adif3.contacts.Qsos;
import uk.m0nom.adifproc.adif3.control.TransformControl;

public interface Adif3RecordTransformer {
    void transform(TransformControl control, TransformResults results, Qsos qsos, Adif3Record rec, int index);
}
