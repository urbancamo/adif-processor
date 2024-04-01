package uk.m0nom.adifproc.adif3.transform;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
public class MyCallsignCheckResults {
    private Set<String> operators;
    private Set<String> stationCallsigns;

    public boolean isOneOperator() { return operators.size() == 1; }
    public boolean isOneStationCallsign() { return stationCallsigns.size() == 1; }

    public String getSingleOperator() {
        String operator = null;
        if (operators.size() == 1 && operators.iterator().hasNext()) {
            operator = operators.iterator().next();
        }
        return operator;
    }

    public String getSingleStationCallsign() {
        String stationCallsign = null;
        if (stationCallsigns.size() == 1 && stationCallsigns.iterator().hasNext()) {
            stationCallsign = stationCallsigns.iterator().next();
        }
        return stationCallsign;
    }

    public String getCallsignsForUserLog() {
        return String.join(", ",operators)
                .concat(String.join(", ", stationCallsigns));
    }
}
