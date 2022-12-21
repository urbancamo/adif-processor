package uk.m0nom.adifproc.adif3.transform;

import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;

import java.util.HashSet;
import java.util.Set;

public class MyCallsignCheck {
    /* Lots of logs have operator or station_callsign missing on some records. If there is a single callsign
     * it is returned here and can be used to fill in the blanks. If there is more than one then this returns an
     * empty Map
     */
    public static MyCallsignCheckResults checkForSingleMyCallsign(Adif3 log) {
        Set<String> stationCallsigns = new HashSet<>();
        Set<String> operators = new HashSet<>();
        MyCallsignCheckResults results = new MyCallsignCheckResults();

        for (Adif3Record rec : log.getRecords()) {
            if (rec.getOperator() != null) {
                operators.add(rec.getOperator());
            }
            if (rec.getStationCallsign() != null) {
                stationCallsigns.add(rec.getStationCallsign());
            }
        }
        results.setStationCallsigns(stationCallsigns);
        results.setOperators(operators);
        return results;
    }
}
