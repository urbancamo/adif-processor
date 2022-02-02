package uk.m0nom.satellite;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

public interface ApSatellite {
    String getName();
    String getDesignator();
    String getIdentifier();
    GlobalCoordinatesWithSourceAccuracy getPosition(GlobalCoordinatesWithSourceAccuracy groundStation, LocalDate date, LocalTime time);
    void updateAdifRec(TransformControl control, Adif3Record rec);
}
