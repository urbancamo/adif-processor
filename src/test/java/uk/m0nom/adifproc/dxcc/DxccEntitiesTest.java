package uk.m0nom.adifproc.dxcc;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.time.LocalDate;
import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;


public class DxccEntitiesTest {

    private static JsonDxccEntities jsonEntities;
    private static DxccEntities entities;
    private static LocalDate qsoDate;

    @BeforeAll
    public static void setup() throws ParseException {
        jsonEntities = new DxccJsonReader().read();
        entities = new DxccEntities();
        entities.setup(jsonEntities);
        qsoDate = LocalDate.of(2022,01,01);
    }

    @Test
    public void haveEntities() {
        assertThat(entities != null);
        assertThat(entities.getDxccEntities().size() == 402);
    }

    @Test
    public void matchSv5DxccEntityUppercase() {
        String prefix = "SV5";
        Collection<DxccEntity> matches = entities.findEntitiesFromPrefix(prefix, qsoDate);
        assertThat(matches.size()).isEqualTo(2);
        DxccEntity firstMatch = matches.iterator().next();
        assertThat(firstMatch.getName()).isEqualTo("Dodecanese");
    }

    @Test
    public void matchSuffix() {
        DxccEntity entity = entities.findDxccEntityFromCallsign("IK2LEY/IS0", qsoDate);
        assertThat(entity.getName()).isEqualTo("Sardinia");
    }

    @Test
    public void bestMatchSv5DxccEntity() {
        DxccEntity match = entities.findDxccEntityFromCallsign("SV5/M0NOM/P", qsoDate);
        assertThat(match.getName()).isEqualTo("Dodecanese");
    }

    @Test
    public void bestEc6DxccEntity() {
        DxccEntity match = entities.findDxccEntityFromCallsign("EC6DX", qsoDate);
        assertThat(match.getName()).isEqualTo("Balearic Islands");
    }

    @Test
    public void testComplexPrefixGeneration() {
        DxccEntity russia = entities.getDxccEntity(15);
        Collection<String> prefixes = entities.getPrefixesForDxccEntity(russia);

    }
    @Test
    public void bestRA9WJVDxccEntity() {
        DxccEntity match = entities.findDxccEntityFromCallsign("RA9WJV", qsoDate);
        assertThat(match.getName()).isEqualTo("European Russia");
    }

    @Test
    public void bestRA2WJVDxccEntity() {
        DxccEntity match = entities.findDxccEntityFromCallsign("RA2WJV", qsoDate);
        assertThat(match.getName()).isEqualTo("Kaliningrad");
    }

    @Test
    public void bestUD9JVCDxccEntity() {
        DxccEntity match = entities.findDxccEntityFromCallsign("UD9JVC", qsoDate);
        assertThat(match.getName()).isEqualTo("Asiatic Russia");
    }

    @Test
    public void bestEA4GTYDxccEntity() {
        DxccEntity match = entities.findDxccEntityFromCallsign("EA4GTY", qsoDate);
        assertThat(match.getName()).isEqualTo("Spain");
    }

    @Test
    public void bestDM6KGDxccEntity() {
        DxccEntity match = entities.findDxccEntityFromCallsign("DM6KG", qsoDate);
        assertThat(match.getName()).isEqualTo("Germany");
    }

}
