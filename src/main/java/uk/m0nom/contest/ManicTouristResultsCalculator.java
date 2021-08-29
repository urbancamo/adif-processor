package uk.m0nom.contest;

import org.marsik.ham.adif.Adif3;
import org.marsik.ham.adif.Adif3Record;
import uk.m0nom.activity.ActivityDatabase;
import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;
import uk.m0nom.activity.sota.SotaSummitInfo;

public class ManicTouristResultsCalculator implements ContestResultCalculator {

    @Override
    public int calculateResult(ActivityDatabases databases, Adif3 log) {
        ActivityDatabase sotaDb = databases.getDatabase(ActivityType.SOTA);

        int points = 0;
        for (Adif3Record record : log.getRecords()) {
            if (record.getMySotaRef() != null) {
                if (record.getMySotaRef().getValue().startsWith("G/LD")) {
                    SotaSummitInfo summitInfo = (SotaSummitInfo) sotaDb.get(record.getMySotaRef().getValue());
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
