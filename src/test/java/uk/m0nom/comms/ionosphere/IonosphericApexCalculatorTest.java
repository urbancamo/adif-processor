package uk.m0nom.comms.ionosphere;

import org.junit.Test;
import uk.m0nom.comms.PropagationApex;

import static org.junit.Assert.assertTrue;

public class IonosphericApexCalculatorTest {

    private void checkResult(PropagationApex apex, double expectedDistanceToApex, double expectedDistanceAcrossEarth) {
        assertTrue(apex != null);

        assertTrue(String.format("Distance to Apex %f doesn't match expected: %f", apex.getDistanceToApex(), expectedDistanceToApex),
                Math.abs(apex.getDistanceToApex() - expectedDistanceToApex) < 0.1);
        assertTrue(String.format("Distance across Earth %f doesn't match expected: %f", apex.getDistanceAcrossEarth(), expectedDistanceAcrossEarth),
                Math.abs(apex.getDistanceAcrossEarth() - expectedDistanceAcrossEarth) < 0.1);
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
        assertTrue(difference < 0.1);
    }

    @Test
    public void testTakeOffAngleCalculator400kmAnd1711km() {
        double takeOffAngle = IonosphericApexCalculator.calculateTakeoffAngleFromDistanceAcrossEarth(1711.3, 400.0);
        double difference = Math.abs(takeOffAngle - 5.0);
        assertTrue(difference < 0.1);
    }

    private PropagationApex testCalculator(double height, double angle) {
        return IonosphericApexCalculator.calculateDistanceOfApex(height, angle);
    }
}
