package uk.m0nom.comms;

import de.micromata.opengis.kml.v_2_2_0.LineString;
import org.gavaghan.geodesy.Ellipsoid;
import org.gavaghan.geodesy.GeodeticCalculator;
import org.gavaghan.geodesy.GeodeticCurve;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adif3.control.TransformControl;
import uk.m0nom.geodesic.GeodesicUtils;

import java.time.LocalTime;
import java.util.List;

public class IonosphericPropagation implements CommsLinkGenerator {
    private final static LocalTime MIDDAY = LocalTime.of(12,0);

    @Override
    public CommsLinkResult getCommsLink(TransformControl control, LineString hfLine, GlobalCoordinates startGc, GlobalCoordinates endGc, Adif3Record rec, double myAltitude, double theirAltitude) {
        /* assume daytime propagation if we don't have a QSO time */
        CommsLinkResult result = new CommsLinkResult();

        LocalTime time = MIDDAY;
        if (rec.getTimeOn() != null) {
            time = rec.getTimeOn();
        }

        double frequencyInKhz = 145 * 1000;

        /* Get Frequency from Band */
        if (rec.getFreq() != null) {
            frequencyInKhz = rec.getFreq() * 1000;
        }
        else if (rec.getBand() != null) {
            frequencyInKhz = (rec.getBand().getLowerFrequency() + ((rec.getBand().getUpperFrequency() - rec.getBand().getLowerFrequency()) / 2.0)) * 1000.0;
        }
        GlobalCoordinates start = new GlobalCoordinates(startGc.getLatitude(), startGc.getLongitude());
        GlobalCoordinates end = new GlobalCoordinates(endGc.getLatitude(), endGc.getLongitude());

        GeodeticCalculator calculator = new GeodeticCalculator();
        GeodeticCurve curve = calculator.calculateGeodeticCurve(Ellipsoid.WGS84, start, end);
        double distance = curve.getEllipsoidalDistance();

        /* work out the ionospheric propagation for this contact */
        double distanceInKm = distance / 1000;
        result.setDistance(distanceInKm);

        double azimuth = curve.getAzimuth();
        double avgAltitude = 0.0;
        double avgAngle = 0.0;
        Propagation mode = rec.getPropMode();
        List<PropagationBounce> bounces = new Ionosphere().getBounces(mode, frequencyInKhz, distanceInKm, time, myAltitude, theirAltitude, control.getHfAntennaTakeoffAngle());

        double skyDistance = GeodesicUtils.addBouncesToLineString(hfLine, bounces, start, end, azimuth, calculator);
        result.setSkyDistance(skyDistance);

        for (PropagationBounce bounce : bounces) {
            avgAltitude += bounce.getHeight();
            avgAngle += bounce.getAngle();
            mode = bounce.getMode();
        }
        result.setMode(mode);
        result.setAltitude(avgAltitude / bounces.size());
        result.setFromAngle(avgAngle / bounces.size());
        result.setBounces(bounces.size());

        return result;
    }
}
