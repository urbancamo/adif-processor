package uk.m0nom.adifproc.activity;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import uk.m0nom.adifproc.adif3.contacts.Qso;
import uk.m0nom.adifproc.adif3.contacts.Station;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DoingSameActivityTest {

    private static Activity stSundayCrag, gummersHow, blackCrag;

    @BeforeAll
    static void setup() {
        ActivityDatabaseService summits = new ActivityDatabaseService();
        summits.loadData();

        var sotaSummits = summits.getDatabase(ActivityType.SOTA);
        var hemaSummits = summits.getDatabase(ActivityType.HEMA);

        stSundayCrag = sotaSummits.get("G/LD-010");
        gummersHow = sotaSummits.get("G/LD-050");
        blackCrag = hemaSummits.get("G/HLD-005");
    }

    @Test
    public void testCrossActivityCheck() {
        Qso crossSotaHemaQso = new Qso();

        Station from = new Station();
        Station to = new Station();
        from.addActivity(stSundayCrag);
        to.addActivity(blackCrag);

        crossSotaHemaQso.setFrom(from);
        crossSotaHemaQso.setTo(to);

        assertFalse(crossSotaHemaQso.doingSameActivity());
    }

    @Test
    public void testSameActivityCheck() {
        Qso sotaQso = new Qso();
        Station from = new Station();
        Station to = new Station();

        from.addActivity(stSundayCrag);
        to.addActivity(gummersHow);

        sotaQso.setFrom(from);
        sotaQso.setTo(to);

        assertTrue(sotaQso.doingSameActivity());
    }

    @Test
    public void testNoActivityCheck() {
        Qso portableQso = new Qso();
        Station from = new Station();
        Station to = new Station();

        from.setActivities(new ArrayList<>());
        to.setActivities(new ArrayList<>());

        portableQso.setFrom(from);
        portableQso.setTo(to);

        assertFalse(portableQso.doingSameActivity());
    }
}
