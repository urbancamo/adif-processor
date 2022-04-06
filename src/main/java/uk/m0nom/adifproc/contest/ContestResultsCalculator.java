package uk.m0nom.adifproc.contest;

import org.marsik.ham.adif.Adif3;
import uk.m0nom.adifproc.activity.ActivityDatabaseService;

import java.util.ArrayList;
import java.util.Collection;

public class ContestResultsCalculator {
    private final Collection<ContestResultCalculator> calculators;
    private final ActivityDatabaseService databases;

    public ContestResultsCalculator(ActivityDatabaseService databases) {
        this.databases = databases;

        calculators = new ArrayList<>();
        calculators.add(new LongDistanceRelationshipContestCalculator());
        calculators.add(new HappyCamperResultsCalculator());
        calculators.add(new ManicTouristResultsCalculator());
        calculators.add(new LakeDistrictLoverResultsCalculator());
        calculators.add(new JackOfAllTradesResultCalculator());

    }

    public String calculateResults(Adif3 log) {
        StringBuilder results = new StringBuilder();

        int i = 0;
        for (ContestResultCalculator calculator : calculators) {
            results.append(calculator.formatResult(calculator.calculateResult(databases, log)));
            i++;
            if (i < calculators.size()) {
                results.append(", ");
            } else {
                results.append("\n");
            }
        }
        return results.toString();
    }
}
