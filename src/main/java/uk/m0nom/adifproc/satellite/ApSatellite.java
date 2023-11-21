package uk.m0nom.adifproc.satellite;

import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.coords.GlobalCoords3D;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

/**
 * Interface for both Low Earth Orbit (LEO) and Geostationary Satellites
 */
public interface ApSatellite {
    String getName();
    String getDesignator();
    String getIdentifier();
    GlobalCoords3D getPosition(GlobalCoords3D groundStation, ZonedDateTime dateTime);
    void updateAdifRec(TransformControl control, Adif3Record rec);
    boolean isGeostationary();
}
