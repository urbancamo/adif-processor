package uk.m0nom.satellite;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.coords.GlobalCoords3D;

import java.time.LocalDate;
import java.time.LocalTime;

public interface ApSatellite {
    String getName();
    String getDesignator();
    String getIdentifier();
    GlobalCoords3D getPosition(GlobalCoords3D groundStation, LocalDate date, LocalTime time);
    void updateAdifRec(TransformControl control, Adif3Record rec);
}
