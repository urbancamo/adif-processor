package uk.m0nom.adif3.contacts;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.marsik.ham.adif.Adif3;
import uk.m0nom.kml.KmlWriter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

@Getter
@Setter
@AllArgsConstructor
public class Qsos {
    private static final Logger logger = Logger.getLogger(Qsos.class.getName());

    private List<Qso> qsos;
    private Map<String, Station> stations;
    private Adif3 log;

    public Qsos() {
        qsos = new ArrayList<>();
        stations = new HashMap<>();
    }

    public Qsos(Adif3 log) {
        this();
        this.log = log;
    }

    public void addQso(Qso qso){
        qsos.add(qso);
        String fromCallsign = qso.getRecord().getStationCallsign();
        if (fromCallsign == null) {
            if (qso.getRecord().getOperator() != null) {
                fromCallsign = qso.getRecord().getOperator();
                logger.warning(String.format("Station callsign not set in ADIF file, falling back to operator: %s", fromCallsign));
                qso.getRecord().setStationCallsign(fromCallsign);
            } else {
                logger.severe(String.format("Could not determine station callsign for ADIF record at %s with %s", qso.getRecord().getTimeOn(), qso.getRecord().getCall()));
            }
        }

        if (!hasStation(fromCallsign)) {
            Station fromStation = new Station(fromCallsign, qso);
            qso.setFrom(fromStation);
            stations.put(fromCallsign, fromStation);
        } else {
            qso.setFrom(getStation(fromCallsign));
        }

        String toCallsign = qso.getRecord().getCall();
        if (!hasStation(toCallsign)) {
            Station toStation = new Station(toCallsign, qso);
            qso.setTo(toStation);
            stations.put(toCallsign, toStation);
        } else {
            qso.setTo(getStation(toCallsign));
        }
    }

    public boolean hasStation(String callsign) {
        return stations.get(callsign) != null;
    }

    public Station getStation(String callsign) {
        return stations.get(callsign);
    }
}
