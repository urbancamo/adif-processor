package uk.m0nom.satellite.satellites;

import org.gavaghan.geodesy.GlobalCoordinates;
import uk.m0nom.satellite.Satellite;
import uk.m0nom.satellite.SatellitePosition;

import java.time.LocalTime;

public class QO100 implements Satellite {
    private final static GlobalCoordinates ESHAIL2_LOCATION = new GlobalCoordinates(0, 25.9);
    private final static double ESHAIL2_HEIGHT = 36000.0 * 1000.0;
    private final static String ESHAIL2_ID = "QO-100";

    private final static SatellitePosition ESHAIL2_POSITION = new SatellitePosition(ESHAIL2_LOCATION, ESHAIL2_HEIGHT);

    @Override
    public String getName() {
        return ESHAIL2_ID;
    }

    @Override
    public SatellitePosition getPosition(LocalTime dateTime) {
        return ESHAIL2_POSITION;
    }
}
