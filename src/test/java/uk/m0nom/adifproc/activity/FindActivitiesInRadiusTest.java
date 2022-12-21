package uk.m0nom.adifproc.activity;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Collection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class FindActivitiesInRadiusTest {

    @Test
    public void testLocalSotaSummits() {
        ActivityDatabaseService summits = new ActivityDatabaseService();
        summits.loadData();

        ActivityDatabase sotaSummits = summits.getDatabase(ActivityType.SOTA);

        Activity stSundayCrag = sotaSummits.get("G/LD-010");
        // Find summits within 5km

        Collection<Activity> localSummits = sotaSummits.findActivitiesInRadius(stSundayCrag, 5, LocalDate.now());
        assertEquals(3, localSummits.size(), String.format("Number of local summits %s not as expected 3", localSummits.size()));
        assertTrue(localSummits.contains(sotaSummits.get("G/LD-007")), "Expected localSummits to contain Fairfield");
        assertTrue(localSummits.contains(sotaSummits.get("G/LD-022")), "Expected localSummits to contain Seat Sandal");
        assertTrue(localSummits.contains(sotaSummits.get("G/LD-003")), "Expected localSummits to contain Helvelly");

        Activity theCalf = sotaSummits.get("G/NP-013");
        localSummits = sotaSummits.findActivitiesInRadius(theCalf, 9, LocalDate.now());
        assertEquals(3, localSummits.size(), String.format("Number of local summits %s not as expected 3", localSummits.size()));
        assertTrue(localSummits.contains(sotaSummits.get("G/NP-019")), "Expected localSummits to contain Yarlside");
        assertTrue(localSummits.contains(sotaSummits.get("G/LD-038")), "Expected localSummits to contain Grayrigg Forest");
        assertTrue(localSummits.contains(sotaSummits.get("G/LD-046")), "Expected localSummits to contain Lambrigg Fell");

    }
}
