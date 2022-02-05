package uk.m0nom.comms.ionosphere;

import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkResult;
import uk.m0nom.comms.PropagationApex;
import uk.m0nom.comms.PropagationUtils;
import uk.m0nom.coords.GlobalCoords3D;
import uk.m0nom.geodesic.GeodesicUtils;

import java.time.LocalTime;
import java.util.List;

public class IonosphereUtils {
    private final static LocalTime MIDDAY = LocalTime.of(12,0);

    private static LocalTime getTime(Adif3Record rec) {
        LocalTime time = MIDDAY;
        if (rec.getTimeOn() != null) {
            time = rec.getTimeOn();
        }
        return time;
    }

    private static double getFrequency(Adif3Record rec) {
        double frequencyInKhz = 145 * 1000;

        /* Get Frequency from Band */
        if (rec.getFreq() != null) {
            frequencyInKhz = rec.getFreq() * 1000;
        } else if (rec.getBand() != null) {
            frequencyInKhz = (rec.getBand().getLowerFrequency() + ((rec.getBand().getUpperFrequency() - rec.getBand().getLowerFrequency()) / 2.0)) * 1000.0;
        }
        return frequencyInKhz;
    }


    public static CommsLinkResult getShortestPath(TransformControl control,
                                                  GlobalCoords3D start, GlobalCoords3D end,
                                                  Adif3Record rec) {

        /* assume daytime propagation if we don't have a QSO time */
        LocalTime time = IonosphereUtils.getTime(rec);
        double frequencyInKhz = IonosphereUtils.getFrequency(rec);

        CommsLinkResult result = PropagationUtils.calculateGeodeticCurve(start, end);

        double azimuth = result.getAzimuth();
        double avgAltitude = 0.0;
        double avgAngle = 0.0;
        Propagation mode = rec.getPropMode();
        List<PropagationApex> apexes = new Ionosphere().getBounces(mode, frequencyInKhz, result.getDistanceInKm(), time, start.getAltitude(), end.getAltitude(), control.getAntenna().getTakeOffAngle());

        double skyDistance = GeodesicUtils.calculatePath(result.getPath(), apexes, start, end, azimuth);
        result.setSkyDistance(skyDistance);

        for (PropagationApex bounce : apexes) {
            avgAltitude += bounce.getApexHeight();
            avgAngle += bounce.getRadiationAngle();
            mode = bounce.getMode();
        }
        result.setApexes(apexes);
        result.setPropagation(mode);
        result.setAltitude(avgAltitude / apexes.size());
        result.setFromAngle(avgAngle / apexes.size());
        result.setBounces(apexes.size());

        return result;
    }
}
