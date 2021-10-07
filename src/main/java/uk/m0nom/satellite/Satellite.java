package uk.m0nom.satellite;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.contacts.Qso;
import uk.m0nom.adif3.control.TransformControl;

import java.time.LocalTime;

public interface Satellite {
    String getName();
    SatellitePosition getPosition(LocalTime dateTime);
    void updateAdifRec(TransformControl control, Adif3Record rec);
}
