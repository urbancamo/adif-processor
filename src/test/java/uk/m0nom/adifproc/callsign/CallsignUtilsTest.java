package uk.m0nom.adifproc.callsign;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.m0nom.adifproc.callsign.CallsignUtils.*;

public class CallsignUtilsTest {
    @Test
    public void getVariantsForM0NOM_slash_Portable() {
        List<Callsign> callsigns = getCallsignVariants("M0NOM/P");
        assertThat(callsigns.get(0).getCallsign()).isEqualTo("M0NOM/P");
    }

    @Test
    public void getUkCallsignVariantsforG() {
        List<Callsign> callsigns = getCallsignVariants("M0NOM");
        Assertions.assertThat(callsigns).hasSize(6);
        assertThat(callsigns.get(0).getCallsign()).isEqualTo("M0NOM");
        assertThat(callsigns.get(1).getCallsign()).isEqualTo("MD0NOM");
        assertThat(callsigns.get(2).getCallsign()).isEqualTo("MG0NOM");
        assertThat(callsigns.get(3).getCallsign()).isEqualTo("MI0NOM");
        assertThat(callsigns.get(4).getCallsign()).isEqualTo("MM0NOM");
        assertThat(callsigns.get(5).getCallsign()).isEqualTo("MW0NOM");
    }

    @Test
    public void getUkCallsignVariantsforGM() {
        List<Callsign> callsigns = getUkCallsignVariants("MM0NOM");
        Assertions.assertThat(callsigns).hasSize(6);
        assertThat(callsigns.get(0).getCallsign()).isEqualTo("MM0NOM");
        assertThat(callsigns.get(1).getCallsign()).isEqualTo("M0NOM");
        assertThat(callsigns.get(2).getCallsign()).isEqualTo("MD0NOM");
        assertThat(callsigns.get(3).getCallsign()).isEqualTo("MG0NOM");
        assertThat(callsigns.get(4).getCallsign()).isEqualTo("MI0NOM");
        assertThat(callsigns.get(5).getCallsign()).isEqualTo("MW0NOM");
    }

    @Test
    public void alternativeAddressTest() {
        String callsign = "M0NOM/A";
        String baseCallsign = "M0NOM";

        List<Callsign> alternatives = getCallsignVariants(callsign);
        assertThat(CallsignUtils.containsCallsign(alternatives,baseCallsign)).isTrue();
    }

    @Test
    public void  englishSotaActivatorInWales() {
        String callsign = "MW0BLA/P";
        String baseCallsign = "M0BLA";
        List<Callsign> alternatives = getCallsignVariants(callsign);
        assertThat(CallsignUtils.containsCallsign(alternatives, baseCallsign)).isTrue();
    }

    @Test
    public void scottishSotaActivatorInWales() {
        String callsign = "MW0VIK/P";
        String baseCallsign = "MM0VIK";
        List<Callsign> alternatives = getCallsignVariants(callsign);
        assertThat(CallsignUtils.containsCallsign(alternatives, baseCallsign)).isTrue();
    }

    @Test
    public void meActivatingnISpainFindPortable() {
        String callsign = "EA7/M0NOM/P";
        String baseCallsign = "M0NOM/P";
        List<Callsign> alternatives = getCallsignVariants(callsign);

        assertThat(CallsignUtils.containsCallsign(alternatives, baseCallsign)).isTrue();
    }

    @Test
    public void meActivatingInSpainFindFixed() {
        String callsign = "EA7/M0NOM/P";
        String baseCallsign = "M0NOM";
        List<Callsign> alternatives = getCallsignVariants(callsign);
        assertThat(CallsignUtils.containsCallsign(alternatives, baseCallsign)).isTrue();
    }

    @Test
    public void meActivatingInSpainCheckOrdering() {
        String callsign = "EA7/M0NOM/P";
        String baseCallsign = "M0NOM";
        List<Callsign> alternatives = getCallsignVariants(callsign);

        int indexOfMeAbroadPortable;
        int indexOfMeAtHomePortable;
        int indexOfMeAtHomeFixed;

        indexOfMeAbroadPortable = indexOfCallsignInList(alternatives, "EA7/M0NOM/P");
        assertThat(indexOfMeAbroadPortable).isNotEqualTo(-1);

        indexOfMeAtHomePortable = indexOfCallsignInList(alternatives,"M0NOM/P");
        assertThat(indexOfMeAtHomePortable).isNotEqualTo(-1);

        indexOfMeAtHomeFixed = indexOfCallsignInList(alternatives,"M0NOM");
        assertThat(indexOfMeAtHomeFixed).isNotEqualTo(-1);

        assertThat(indexOfMeAbroadPortable).isLessThan(indexOfMeAtHomePortable);
        assertThat(indexOfMeAtHomeFixed).isGreaterThan(indexOfMeAtHomePortable);

        assertThat(containsCallsign(alternatives,baseCallsign)).isTrue();
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

