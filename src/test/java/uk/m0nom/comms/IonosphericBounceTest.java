package uk.m0nom.comms;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalTime;
import java.util.List;

public class IonosphericBounceTest {

    private Ionosphere ionosphere = new Ionosphere();

    private final static LocalTime AFTERNOON = LocalTime.of(15,0);
    private final static LocalTime NIGHT = LocalTime.of(3,0);

    @Test
    public void HfSkywaveTest() {

        /** Contact on 20m at 1000 km in the DAY */
        List<PropagationBounce> bounces = ionosphere.getBounces(1430, 1000,
                AFTERNOON, 0.0, 0.0, Ionosphere.HF_ANTENNA_DEFAULT_TAKEOFF_ANGLE);
        Assert.assertEquals(bounces.size(), 1);

        /** Contact on 20m at 5000 km in the DAY */
        bounces = ionosphere.getBounces(1430, 5000,
                AFTERNOON, 0.0, 0.0, Ionosphere.HF_ANTENNA_DEFAULT_TAKEOFF_ANGLE);
        Assert.assertEquals(bounces.size(), 2);
    }
}
