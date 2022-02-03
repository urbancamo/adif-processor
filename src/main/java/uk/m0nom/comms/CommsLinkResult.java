package uk.m0nom.comms;

import lombok.Getter;
import lombok.Setter;
import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.coords.GlobalCoords3D;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommsLinkResult {
    private int bounces;
    private double altitude;
    private double base;
    private double distanceInKm;
    private double fromAngle;
    private double toAngle;
    private double skyDistance;
    private Propagation propagation;
    private double azimuth;
    private List<GlobalCoords3D> path;
    private GlobalCoords3D satellitePosition;
    private String error;

    public CommsLinkResult() {
        path = new ArrayList<>();
    }

    public boolean isValid() {
        return error == null;
    }
}

