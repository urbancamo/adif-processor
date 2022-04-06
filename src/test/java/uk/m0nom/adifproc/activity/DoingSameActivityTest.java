package uk.m0nom.adifproc.activity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.contacts.Station;

import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DoingSameActivityTest {

    private static HashMap<ActivityType, Activity> stSundayCragMap;
    private static HashMap<ActivityType, Activity> gummersHowMap;
    private static HashMap<ActivityType, Activity> hemaMap;

    @BeforeAll
    static void setup() {
        ActivityDatabaseService summits = new ActivityDatabaseService();
        summits.loadData();

        var sotaSummits = summits.getDatabase(ActivityType.SOTA);
        var hemaSummits = summits.getDatabase(ActivityType.HEMA);

        var stSundayCrag = sotaSummits.get("G/LD-010");
        var gummersHow = sotaSummits.get("G/LD-050");
        var blackCrag = hemaSummits.get("G/HLD-005");

        stSundayCragMap = new HashMap<>();
        stSundayCragMap.put(ActivityType.SOTA, stSundayCrag);
        gummersHowMap = new HashMap<>();
        gummersHowMap.put(ActivityType.SOTA, gummersHow);

        hemaMap = new HashMap<>();
        hemaMap.put(ActivityType.HEMA, blackCrag);
    }

    @Test
    public void testCrossActivityCheck() {
        Qso crossSotaHemaQso = new Qso();

        Station from = new Station();
        Station to = new Station();
        from.setActivities(stSundayCragMap);
        to.setActivities(hemaMap);

        crossSotaHemaQso.setFrom(from);
        crossSotaHemaQso.setTo(to);

        assertFalse(crossSotaHemaQso.doingSameActivity());
    }

    @Test
    public void testSameActivityCheck() {
        Qso sotaQso = new Qso();
        Station from = new Station();
        Station to = new Station();

        from.setActivities(stSundayCragMap);
        to.setActivities(gummersHowMap);

        sotaQso.setFrom(from);
        sotaQso.setTo(to);

        assertTrue(sotaQso.doingSameActivity());
    }

    @Test
    public void testNoActivityCheck() {
        Qso portableQso = new Qso();
        Station from = new Station();
        Station to = new Station();

        from.setActivities(new HashMap<>());
        to.setActivities(new HashMap<>());

        portableQso.setFrom(from);
        portableQso.setTo(to);

        assertFalse(portableQso.doingSameActivity());
    }
}
