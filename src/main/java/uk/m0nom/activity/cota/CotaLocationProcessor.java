package uk.m0nom.activity.cota;

import uk.m0nom.activity.ActivityDatabases;
import uk.m0nom.activity.ActivityType;

import java.io.FileNotFoundException;

public class CotaLocationProcessor {

    public static void main(String args[]) throws FileNotFoundException {
        ActivityDatabases summits = new ActivityDatabases();
        summits.loadData();

        new CotaCsvWriter().write(summits.getDatabase(ActivityType.COTA));
    }
}
