package uk.m0nom.adifproc.comms.ionosphere;

import org.junit.jupiter.api.Test;
import uk.m0nom.adifproc.comms.PropagationApex;

import static org.assertj.core.api.Assertions.assertThat;

public class IonosphericApexCalculatorTest {

    private void checkResult(PropagationApex apex, double expectedDistanceToApex, double expectedDistanceAcrossEarth) {
        assertThat(apex).isNotNull();

        assertThat(Math.abs(apex.getDistanceToApex() - expectedDistanceToApex)).isLessThan(0.1);
        assertThat(Math.abs(apex.getDistanceAcrossEarth() - expectedDistanceAcrossEarth)).isLessThan(0.1);
    }

    @Test
    public void test400kmAnd90Degrees() {
        PropagationApex apex = testCalculator(400.0, 89.999);
        checkResult(apex, 400, 0);
    }

    @Test
    public void test400kmAnd5Degrees() {
        PropagationApex apex = testCalculator(400.0, 5.0);
        checkResult(apex, 1803.8, 1711.3);
    }

    @Test
    public void test400kmAnd45Degrees() {
        PropagationApex apex = testCalculator(400.0, 45.0);
        checkResult(apex, 549.9, 366.1);
    }

    @Test
    public void testTakeOffAngleCalculator400kmAnd366km() {
        double takeOffAngle = IonosphericApexCalculator.calculateTakeoffAngleFromDistanceAcrossEarth(366.1, 400.0);
        double difference = Math.abs(takeOffAngle - 45);
        assertThat(difference).isLessThan(0.1);
    }

    @Test
    public void testTakeOffAngleCalculator400kmAnd1711km() {
        double takeOffAngle = IonosphericApexCalculator.calculateTakeoffAngleFromDistanceAcrossEarth(1711.3, 400.0);
        double difference = Math.abs(takeOffAngle - 5.0);
        assertThat(difference).isLessThan(0.1);
    }

    private PropagationApex testCalculator(double height, double angle) {
        return IonosphericApexCalculator.calculateDistanceOfApex(height, angle);
    }
}
