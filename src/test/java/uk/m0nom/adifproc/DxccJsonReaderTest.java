package uk.m0nom.adifproc;

import org.junit.jupiter.api.Test;
import uk.m0nom.adifproc.dxcc.DxccEntities;
import uk.m0nom.adifproc.dxcc.DxccJsonReader;
import uk.m0nom.adifproc.dxcc.JsonDxccEntities;

import static org.assertj.core.api.Assertions.assertThat;

public class DxccJsonReaderTest {
    @Test
    public void loadJsonTest() {
        DxccJsonReader reader = new DxccJsonReader();
        JsonDxccEntities entities = reader.read();
        assertThat(entities).isNotNull();
    }
}
