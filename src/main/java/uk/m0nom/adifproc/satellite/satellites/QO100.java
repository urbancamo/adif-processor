package uk.m0nom.adifproc.satellite.satellites;

import org.apache.commons.lang3.StringUtils;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Band;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.coords.GlobalCoords3D;
import uk.m0nom.adifproc.satellite.ApSatellite;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

public class QO100 implements ApSatellite {
    private final static GlobalCoordinates ESHAIL2_LOCATION = new GlobalCoordinates(0, 25.9);
    private final static double ESHAIL2_HEIGHT = 36000.0 * 1000.0;
    private final static String ESHAIL2_NAME = "Es’hail-2";
    private final static String ESHAIL2_DESIGNATOR = "QO-100";

    private final static GlobalCoords3D ESHAIL2_POSITION = new GlobalCoords3D(ESHAIL2_LOCATION, ESHAIL2_HEIGHT);

    @Override
    public String getIdentifier() {
        if (StringUtils.isNotBlank(getDesignator())) {
            return String.format("%s: %s", getDesignator(), getName());
        }
        return getName();
    }

    @Override
    public String getName() {
        return ESHAIL2_NAME;
    }

    @Override
    public String getDesignator() {
        return ESHAIL2_DESIGNATOR;
    }

    @Override
    public GlobalCoords3D getPosition(GlobalCoords3D loc, ZonedDateTime dateTime) {
        return ESHAIL2_POSITION;
    }

    @Override
    public void updateAdifRec(TransformControl control, Adif3Record rec) {
        rec.setBand(Band.BAND_13cm);
        rec.setBandRx(Band.BAND_3cm);
    }

    @Override
    public boolean isGeostationary() {
        return true;
    }
}
