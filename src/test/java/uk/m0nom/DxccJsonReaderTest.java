package uk.m0nom;

import org.junit.jupiter.api.Test;
import uk.m0nom.dxcc.DxccEntities;
import uk.m0nom.dxcc.DxccJsonReader;

import static org.assertj.core.api.Assertions.assertThat;

public class DxccJsonReaderTest {
    @Test
    public void loadJsonTest() {
        DxccJsonReader reader = new DxccJsonReader();
        DxccEntities entities = reader.read();
        assertThat(entities).isNotNull();
    }
}
