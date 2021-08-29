package uk.m0nom.contest;

import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;

public class LongDistanceRelationshipContestCalculator implements ContestResultCalculator {
    @Override
    public int calculateResult(ActivityDatabases databases, Adif3 log) {
        double totalDistance = 0;
        for (Adif3Record record : log.getRecords()) {
            if (record.getMySotaRef() != null && record.getMySotaRef().getValue().startsWith("G/LD")) {
                if (record.getDistance() != null) {
                    totalDistance += record.getDistance();
                }
            }
        }
       Double t = Math.ceil(totalDistance);
        return t.intValue();
    }

    public String formatResult(int totalDistance) {
        return String.format("LongDistanceRelationship: %d", totalDistance);
    }
}
