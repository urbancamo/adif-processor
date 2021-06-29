package uk.m0nom.adif3.contacts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
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
}
