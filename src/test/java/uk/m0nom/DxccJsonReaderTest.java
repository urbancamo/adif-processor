package uk.m0nom;

import org.junit.Test;
import uk.m0nom.dxcc.DxccEntities;
import uk.m0nom.dxcc.DxccJsonReader;

import java.io.IOException;

import static org.junit.Assert.assertNotNull;

public class DxccJsonReaderTest {
    @Test
    public void loadJsonTest() throws IOException {
        DxccJsonReader reader = new DxccJsonReader();
        DxccEntities entities = reader.read();
        assertNotNull(entities);
    }
}
