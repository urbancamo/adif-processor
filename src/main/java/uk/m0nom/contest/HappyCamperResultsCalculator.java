package uk.m0nom.contest;

import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.sota.SotaInfo;

public class HappyCamperResultsCalculator implements ContestResultCalculator {

    @Override
    public int calculateResult(ActivityDatabases databases, Adif3 log) {
        ActivityDatabase sotaDb = databases.getDatabase(ActivityType.SOTA);

        int s2sPoints = 0;
        for (Adif3Record record : log.getRecords()) {
            if (record.getSotaRef() != null && record.getMySotaRef() != null) {
                if (record.getMySotaRef().getValue().startsWith("G/LD")) {
                    SotaInfo summitInfo = (SotaInfo) sotaDb.get(record.getSotaRef().getValue());
                    if (summitInfo != null) {
                        s2sPoints += summitInfo.getPoints();
                    }
                }
            }
        }
        return s2sPoints;
    }

    public String formatResult(int points) {
        return String.format("HappyCamper: %d", points);
    }
}
