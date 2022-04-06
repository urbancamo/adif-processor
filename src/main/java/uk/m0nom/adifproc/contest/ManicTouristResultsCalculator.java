package uk.m0nom.adifproc.contest;

import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.adifproc.activity.ActivityDatabase;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;
import uk.m0nom.adifproc.activity.ActivityType;
import uk.m0nom.adifproc.activity.sota.SotaInfo;

public class ManicTouristResultsCalculator implements ContestResultCalculator {

    @Override
    public int calculateResult(ActivityDatabaseService databases, Adif3 log) {
        ActivityDatabase sotaDb = databases.getDatabase(ActivityType.SOTA);

        int points = 0;
        for (Adif3Record record : log.getRecords()) {
            if (record.getMySotaRef() != null) {
                if (record.getMySotaRef().getValue().startsWith("G/LD")) {
                    SotaInfo summitInfo = (SotaInfo) sotaDb.get(record.getMySotaRef().getValue());
                    if (summitInfo != null) {
                        points = summitInfo.getPoints();
                    }
                }
            }
        }
        return points;
    }

    public String formatResult(int points) {
        return String.format("ManicTourist: %d", points);
    }
}
