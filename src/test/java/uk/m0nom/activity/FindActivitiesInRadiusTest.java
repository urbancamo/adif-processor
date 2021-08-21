package uk.m0nom.activity;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.assertTrue;

public class FindActivitiesInRadiusTest {
    private ActivityDatabases summits;

    @Test
    public void testLocalSotaSummits() {
        summits = new ActivityDatabases();
        summits.loadData();

        ActivityDatabase sotaSummits = summits.getDatabase(ActivityType.SOTA);

        Activity stSundayCrag = sotaSummits.get("G/LD-010");
        // Find summits within 5km

        Collection<Activity> localSummits = sotaSummits.findActivitiesInRadius(stSundayCrag, 5000);
        assertTrue(String.format("Number of local summits %s not as expected 3", localSummits.size()), localSummits.size() == 3);
        assertTrue("Expected localSummits to contain Fairfield", localSummits.contains(sotaSummits.get("G/LD-007")));
        assertTrue("Expected localSummits to contain Seat Sandal", localSummits.contains(sotaSummits.get("G/LD-022")));
        assertTrue("Expected localSummits to contain Helvelly", localSummits.contains(sotaSummits.get("G/LD-003")));

        Activity theCalf = sotaSummits.get("G/NP-013");
        localSummits = sotaSummits.findActivitiesInRadius(theCalf, 9000);
        assertTrue(String.format("Number of local summits %s not as expected 3", localSummits.size()), localSummits.size() == 3);
        assertTrue("Expected localSummits to contain Yarlside", localSummits.contains(sotaSummits.get("G/NP-019")));
        assertTrue("Expected localSummits to contain Grayrigg Forest", localSummits.contains(sotaSummits.get("G/LD-038")));
        assertTrue("Expected localSummits to contain Lambrigg Fell", localSummits.contains(sotaSummits.get("G/LD-046")));

    }

    @Test
    public void testLocalWotaSummits() {

    }
}
