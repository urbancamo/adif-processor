package uk.m0nom.adif3.contacts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.gavaghan.geodesy.GlobalCoordinates;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.qrz.QrzCallsign;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class Station {
    private String callsign;
    private QrzCallsign qrzInfo;
    private List<Qso> qsos;

    private String sotaId;
    private String hemaId;
    private String wotaId;
    private String potaId;
    private String grid;
    private GlobalCoordinates coordinates;

    public Station() {
        qsos = new ArrayList<>();
    }

    public Station(String callsign, Qso initialQso) {
        this();
        this.callsign = callsign;
        addQso(initialQso);
    }

    public void addQso(Qso qso) {
        qsos.add(qso);
    }

    public boolean isSota() { return sotaId != null; }
    public boolean isHema() { return hemaId != null; }
    public boolean isWota() { return wotaId != null; }
    public boolean isPota() { return potaId != null; }
}
