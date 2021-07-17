package uk.m0nom.callsign;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertTrue;
import static uk.m0nom.callsign.CallsignUtils.containsCallsign;
import static uk.m0nom.callsign.CallsignUtils.getUkCallsignVariants;

public class CallsignUtilsTest {
    @Test
    public void getUkCallsignVariantsforG() {
        List<Callsign> callsigns = getUkCallsignVariants("M0NOM");
        assertTrue("Variants doesn't comtain correct number", callsigns.size() == 5);
        assertTrue(StringUtils.equals(callsigns.get(0).getCallsign(), "MD0NOM"));
        assertTrue(StringUtils.equals(callsigns.get(1).getCallsign(), "MG0NOM"));
        assertTrue(StringUtils.equals(callsigns.get(2).getCallsign(), "MI0NOM"));
        assertTrue(StringUtils.equals(callsigns.get(3).getCallsign(), "MM0NOM"));
        assertTrue(StringUtils.equals(callsigns.get(4).getCallsign(), "MW0NOM"));
    }

    @Test
    public void getUkCallsignVariantsforGM() {
        List<Callsign> callsigns = getUkCallsignVariants("MM0NOM");
        assertTrue("Variants doesn't comtain correct number", callsigns.size() == 5);
        assertTrue(StringUtils.equals(callsigns.get(0).getCallsign(), "M0NOM"));
        assertTrue(StringUtils.equals(callsigns.get(1).getCallsign(), "MD0NOM"));
        assertTrue(StringUtils.equals(callsigns.get(2).getCallsign(), "MG0NOM"));
        assertTrue(StringUtils.equals(callsigns.get(3).getCallsign(), "MI0NOM"));
        assertTrue(StringUtils.equals(callsigns.get(4).getCallsign(), "MW0NOM"));
    }

    @Test
    public void alternativeAddressTest() {
        String callsign = "M0NOM/A";
        String baseCallsign = "M0NOM";

        List<Callsign> alternatives = CallsignUtils.getCallsignVariants(callsign);
        assertTrue(String.format("Didn't detect correct base address %s for %s", baseCallsign, callsign),
                CallsignUtils.containsCallsign(alternatives, "M0NOM"));
    }

    @Test
    public void  englishSotaActivatorInWales() {
        String callsign = "MW0BLA/P";
        String baseCallsign = "M0BLA";
        List<Callsign> alternatives = CallsignUtils.getCallsignVariants(callsign);
        assertTrue(String.format("Didn't detect correct base address %s for %s", baseCallsign, callsign),
                CallsignUtils.containsCallsign(alternatives, baseCallsign));
    }

    @Test
    public void scottishSotaActivatorInWales() {
        String callsign = "MW0VIK/P";
        String baseCallsign = "MM0VIK";
        List<Callsign> alternatives = CallsignUtils.getCallsignVariants(callsign);
        assertTrue(String.format("Didn't detect correct base address %s for %s", baseCallsign, callsign),
                CallsignUtils.containsCallsign(alternatives, baseCallsign));
    }

    @Test
    public void meActivatingnISpainFindPortable() {
        String callsign = "EA7/M0NOM/P";
        String baseCallsign = "M0NOM/P";
        List<Callsign> alternatives = CallsignUtils.getCallsignVariants(callsign);

        assertTrue(String.format("Didn't detect correct base address %s for %s", baseCallsign, callsign),
                CallsignUtils.containsCallsign(alternatives, baseCallsign));
    }

    @Test
    public void meActivatingInSpainFindFixed() {
        String callsign = "EA7/M0NOM/P";
        String baseCallsign = "M0NOM";
        List<Callsign> alternatives = CallsignUtils.getCallsignVariants(callsign);
        assertTrue(String.format("Didn't detect correct base address %s for %s", baseCallsign, callsign),
                CallsignUtils.containsCallsign(alternatives, baseCallsign));
    }

    @Test
    public void meActivatingInSpainCheckOrdering() {
        String callsign = "EA7/M0NOM/P";
        String baseCallsign = "M0NOM";
        List<Callsign> alternatives = CallsignUtils.getCallsignVariants(callsign);

        int indexOfMeAbroadPortable;
        int indexOfMeAtHomePortable;
        int indexOfMeAtHomeFixed;

        indexOfMeAbroadPortable = indexOfCallsignInList(alternatives, "EA7/M0NOM");
        assertTrue(String.format("Didn't determine that EA7/M0NOM is a variant of %s", callsign), indexOfMeAbroadPortable != -1);

        indexOfMeAtHomePortable = indexOfCallsignInList(alternatives,"M0NOM/P");
        assertTrue(String.format("Didn't determine that M0NOM/P is a variant of %s", callsign), indexOfMeAtHomePortable != -1);

        indexOfMeAtHomeFixed = indexOfCallsignInList(alternatives,"M0NOM");
        assertTrue(String.format("Didn't determine that M0NOM is a variant of %s", callsign), indexOfMeAtHomeFixed != -1);

        assertTrue("M0NOM/P should not be higher in the list than EA7/M0NOM/P", indexOfMeAbroadPortable < indexOfMeAtHomePortable);
        assertTrue("M0NOM should not be higher in the list than M0NOM/P", indexOfMeAtHomeFixed > indexOfMeAtHomePortable);
        assertTrue("M0NOM should not be higher in the list than EA7/M0NOM/P", indexOfMeAtHomeFixed > indexOfMeAbroadPortable);

        assertTrue(String.format("Didn't detect correct base address %s for %s", baseCallsign, callsign),
                containsCallsign(alternatives,baseCallsign));
    }

    private int indexOfCallsignInList(List<Callsign> variants, String callsign) {
         for (int i = 0; i < variants.size(); i++) {
             Callsign op = variants.get(i);
            if (StringUtils.equals(op.getCallsign(), callsign)) {
                return i;
            }
        }
         return -1;
    }
}

