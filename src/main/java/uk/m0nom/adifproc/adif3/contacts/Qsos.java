package uk.m0nom.adifproc.adif3.contacts;

import lombok.Data;
import org.marsik.ham.adif.Adif3;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Captures a list of QSOs, typically in chronological order
 */
@Data
public class Qsos {
    private static final Logger logger = Logger.getLogger(Qsos.class.getName());

    private List<Qso> qsos;
    private Adif3 log;

    public Qsos() {
        qsos = new ArrayList<>();
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
                //logger.warning(String.format("Station callsign not set in ADIF file, falling back to operator: %s", fromCallsign));
                qso.getRecord().setStationCallsign(fromCallsign);
            } else {
                logger.severe(String.format("Could not determine station callsign for ADIF record at %s with %s", qso.getRecord().getTimeOn(), qso.getRecord().getCall()));
            }
        }

        Station fromStation = new Station(fromCallsign, qso);
        qso.setFrom(fromStation);

        String toCallsign = qso.getRecord().getCall();
        Station toStation = new Station(toCallsign, qso);
        qso.setTo(toStation);

    }
}
