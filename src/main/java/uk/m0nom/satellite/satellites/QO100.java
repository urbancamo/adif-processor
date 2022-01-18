package uk.m0nom.satellite.satellites;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Band;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.coords.GlobalCoordinatesWithSourceAccuracy;
import uk.m0nom.satellite.ApSatellite;
import uk.m0nom.satellite.SatellitePosition;

import java.time.LocalDate;
import java.time.LocalTime;

public class QO100 implements ApSatellite {
    private final static GlobalCoordinates ESHAIL2_LOCATION = new GlobalCoordinates(0, 25.9);
    private final static double ESHAIL2_HEIGHT = 36000.0 * 1000.0;
    private final static String ESHAIL2_ID = "QO-100";

    private final static GlobalCoordinatesWithSourceAccuracy ESHAIL2_POSITION = new GlobalCoordinatesWithSourceAccuracy(ESHAIL2_LOCATION, ESHAIL2_HEIGHT);

    @Override
    public String getName() {
        return ESHAIL2_ID;
    }

    @Override
    public GlobalCoordinatesWithSourceAccuracy getPosition(GlobalCoordinatesWithSourceAccuracy loc, LocalDate date, LocalTime time) {
        return ESHAIL2_POSITION;
    }

    @Override
    public void updateAdifRec(TransformControl control, Adif3Record rec) {
        rec.setBand(Band.BAND_13cm);
        rec.setBandRx(Band.BAND_3cm);
    }
}
