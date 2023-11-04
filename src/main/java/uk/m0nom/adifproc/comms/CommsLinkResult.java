package uk.m0nom.adifproc.comms;

import lombok.Getter;
import lombok.Setter;
import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.adifproc.coords.GlobalCoords3D;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CommsLinkResult {
    private GlobalCoords3D start, end;
    private int bounces;
    private double altitude;
    private double base;
    private double distanceInKm;
    private double fromAngle;
    private double toAngle;
    private double skyDistance;
    private Propagation propagation;
    private double azimuth;
    private List<PropagationApex> apexes;
    private List<GlobalCoords3D> path;
    private GlobalCoords3D satellitePosition;
    private String error = null;
    private String unknownSatellite = null;
    private boolean unknownSatellitePass = false;
    private boolean unsupportedPropagationMode = false;
    private Propagation unsupportedPropagation;

    public CommsLinkResult() {
        path = new ArrayList<>();
    }

    public CommsLinkResult(String error) {
        this.error = error;
    }

    public CommsLinkResult(GlobalCoords3D start, GlobalCoords3D end) {
        this();
        this.start = start;
        this.end = end;
    }

    public boolean isValid() {
        return error == null && unknownSatellite == null && !unknownSatellitePass;
    }
}

