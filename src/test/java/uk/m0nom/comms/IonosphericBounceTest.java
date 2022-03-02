package uk.m0nom.comms;

import org.junit.jupiter.api.Test;
import org.marsik.ham.adif.enums.Propagation;
import uk.m0nom.comms.ionosphere.Ionosphere;

import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class IonosphericBounceTest {

    private final Ionosphere ionosphere = new Ionosphere();

    private final static LocalTime AFTERNOON = LocalTime.of(15,0);

    @Test
    public void HfSkywaveTest20mBand1000km() {

        /* Contact on 20m at 1000 km in the DAY */
        List<PropagationApex> bounces = ionosphere.getBounces(Propagation.F2_REFLECTION, 1430, 1000,
                AFTERNOON, 0.0, 0.0, Ionosphere.HF_ANTENNA_DEFAULT_TAKEOFF_ANGLE);
        assertThat(bounces.size()).isEqualTo(1);
    }

    @Test
    public void HfSkywaveTest20mBand5000km() {
        /* Contact on 20m at 5000 km in the DAY */
        List<PropagationApex> bounces = ionosphere.getBounces(Propagation.F2_REFLECTION, 1430, 5000,
                AFTERNOON, 0.0, 0.0, Ionosphere.HF_ANTENNA_DEFAULT_TAKEOFF_ANGLE);
        assertThat(bounces.size()).isEqualTo(2);
    }
}
