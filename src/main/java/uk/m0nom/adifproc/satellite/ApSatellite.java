package uk.m0nom.adifproc.satellite;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.coords.GlobalCoords3D;

import java.time.LocalDate;
import java.time.LocalTime;

/**
 * Interface for both Low Earth Orbit (LEO) and Geostationary Satellites
 */
public interface ApSatellite {
    String getName();
    String getDesignator();
    String getIdentifier();
    GlobalCoords3D getPosition(GlobalCoords3D groundStation, LocalDate date, LocalTime time);
    void updateAdifRec(TransformControl control, Adif3Record rec);
}
