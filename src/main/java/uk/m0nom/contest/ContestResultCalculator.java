package uk.m0nom.contest;

import org.marsik.ham.adif.Adif3;
import uk.m0nom.activity.ActivityDatabases;

public interface ContestResultCalculator {
    int calculateResult(ActivityDatabases databases, Adif3 log);
    String formatResult(int points);
}
