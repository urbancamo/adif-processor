package uk.m0nom.adifproc.contest;

import org.marsik.ham.adif.Adif3;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;

public interface ContestResultCalculator {
    int calculateResult(ActivityDatabaseService databases, Adif3 log);
    String formatResult(int points);
}
