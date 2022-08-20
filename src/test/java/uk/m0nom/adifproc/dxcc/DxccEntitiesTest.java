package uk.m0nom.adifproc.dxcc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


public class DxccEntitiesTest {

    private static DxccEntities entities;

    @BeforeAll
    public static void setup() {
        entities = new DxccJsonReader().read();
    }

    @Test
    public void haveEntities() {
        assertThat(entities != null);
        assertThat(entities.getDxccEntities().size() == 402);
    }

    @Test
    public void matchSv5DxccEntityUppercase() {
        String prefix = "SV5";
        Collection<DxccEntity> matches = entities.findEntitiesFromPrefix(prefix);
        assertThat(matches.size()).isEqualTo(2);
        DxccEntity firstMatch = matches.iterator().next();
        assertThat(firstMatch.getName()).isEqualTo("Dodecanese");
    }

    @Test
    public void bestMatchSv5DxccEntity() {
        DxccEntity match = entities.findDxccEntityFromCallsign("SV5/M0NOM/P");
        assertThat(match.getName()).isEqualTo("Dodecanese");
    }

    @Test
    public void bestEc6DxccEntity() {
        DxccEntity match = entities.findDxccEntityFromCallsign("EC6DX");
        assertThat(match.getName()).isEqualTo("Balearic Islands");
    }
}
