package uk.m0nom.contest;

import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.sota.SotaInfo;

public class LakeDistrictLoverResultsCalculator implements ContestResultCalculator {

    @Override
    public int calculateResult(ActivityDatabases databases, Adif3 log) {
        ActivityDatabase sotaDb = databases.getDatabase(ActivityType.SOTA);

        int summitPoints = 0;
        for (Adif3Record record : log.getRecords()) {
            if (record.getSotaRef() != null) {
                if (record.getMySotaRef() == null || !record.getMySotaRef().getValue().startsWith("G/LD")) {
                    SotaInfo summitInfo = (SotaInfo) sotaDb.get(record.getSotaRef().getValue());
                    if (summitInfo != null && summitInfo.getRef().startsWith("G/LD")) {
                        summitPoints += summitInfo.getPoints();
                    }
                }
            }
        }
        return summitPoints;
    }

    public String formatResult(int summitPoints) {
        return String.format("LakeDistrictLover: %d", summitPoints);
    }
}
