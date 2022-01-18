package uk.m0nom.comms.ionosphere;

import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.comms.CommsLinkResult;
import uk.m0nom.comms.PropagationApex;
import uk.m0nom.comms.PropagationUtils;
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


    public static CommsLinkResult getShortestPath(TransformControl control, LineString hfLine,
                                       GlobalCoordinates start, GlobalCoordinates end,
                                       Adif3Record rec, double myAltitude, double theirAltitude) {

        /* assume daytime propagation if we don't have a QSO time */
        LocalTime time = IonosphereUtils.getTime(rec);
        double frequencyInKhz = IonosphereUtils.getFrequency(rec);

        CommsLinkResult result = PropagationUtils.calculateGeodeticCurve(start, end);

        double azimuth = result.getAzimuth();
        double avgAltitude = 0.0;
        double avgAngle = 0.0;
        Propagation mode = rec.getPropMode();
        List<PropagationApex> bounces = new Ionosphere().getBounces(mode, frequencyInKhz, result.getDistanceInKm(), time, myAltitude, theirAltitude, control.getAntenna().getTakeOffAngle());

        double skyDistance = GeodesicUtils.addBouncesToLineString(hfLine, bounces, start, end, azimuth);
        result.setSkyDistance(skyDistance);

        for (PropagationApex bounce : bounces) {
            avgAltitude += bounce.getApexHeight();
            avgAngle += bounce.getRadiationAngle();
            mode = bounce.getMode();
        }
        result.setPropagation(mode);
        result.setAltitude(avgAltitude / bounces.size());
        result.setFromAngle(avgAngle / bounces.size());
        result.setBounces(bounces.size());

        return result;
    }
}
