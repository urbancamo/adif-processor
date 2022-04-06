package uk.m0nom.adifproc.contest;

import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.activity.ActivityDatabase;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.activity.sota.SotaInfo;

public class LakeDistrictLoverResultsCalculator implements ContestResultCalculator {

    @Override
    public int calculateResult(ActivityDatabaseService databases, Adif3 log) {
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
