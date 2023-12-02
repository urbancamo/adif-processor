package uk.m0nom.adifproc.comms;

import org.marsik.ham.adif.Adif3Record;
import org.marsik.ham.adif.enums.AntPath;
import org.marsik.ham.adif.enums.Mode;
import org.marsik.ham.adif.enums.Propagation;
import org.springframework.stereotype.Service;
import uk.m0nom.adifproc.adif3.control.TransformControl;
import uk.m0nom.adifproc.comms.ionosphere.IonosphereUtils;
import uk.m0nom.adifproc.comms.ionosphere.LongPath;
import uk.m0nom.adifproc.comms.ionosphere.ShortPath;
import uk.m0nom.adifproc.coords.GlobalCoords3D;

/**
 * Calculates the path of propagation between two points on the earth based on a propagation mode
 */
@Service
public class CommsVisualizationService implements CommsLinkGenerator {
    private final SatellitePropagationService satellitePropagationService;

    public CommsVisualizationService(SatellitePropagationService satellitePropagationService) {
        this.satellitePropagationService = satellitePropagationService;
    }

    public CommsLinkResult getCommunicationsLink(TransformControl control, GlobalCoords3D startGc, GlobalCoords3D endGc,
                                                 Adif3Record rec) {
        CommsLinkResult result = null;
        boolean unsupportedPropagationMode = false;
        Propagation overridenPropagationMode = rec.getPropMode();
        Propagation propagationModeOverride = Propagation.F2_REFLECTION;

        // See if the propagation mode used is defined in the record
        if (rec.getPropMode() != null) {
            // We honour satellite mode here
            switch (rec.getPropMode()) {
                case INTERNET:
                    result = new InternetPropagation().getCommunicationsLink(control, startGc, endGc, rec);
                    break;
                case SATELLITE:
                    result = satellitePropagationService.getCommunicationsLink(control, startGc, endGc, rec);
                    break;
                case TROPOSPHERIC_DUCTING:
                    result = new TroposphericDuctingPropagation().getCommunicationsLink(control, startGc, endGc, rec);
                    break;
                case F2_REFLECTION:
                    break;
                default:
                    unsupportedPropagationMode = true;
                    break;
            }
        }

        if (unsupportedPropagationMode) {
            rec.setPropMode(propagationModeOverride);
        }

        if (result == null) {
            if (rec.getAntPath() == AntPath.LONG) {
                result = new LongPath().getCommunicationsLink(control, startGc, endGc, rec);
            } else {
                result = new ShortPath().getCommunicationsLink(control, startGc, endGc, rec);
            }
        }

        if (unsupportedPropagationMode) {
            result.setUnsupportedPropagationMode(true);
            result.setUnsupportedPropagation(overridenPropagationMode);
        }
        return result;
    }
}