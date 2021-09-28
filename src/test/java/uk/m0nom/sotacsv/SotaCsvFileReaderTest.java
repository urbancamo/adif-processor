package uk.m0nom.sotacsv;

import org.junit.Test;
import org.marsik.ham.adif.Adif3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class SotaCsvFileReaderTest {

    @Test
    public void testSotaCsvConverter() throws IOException {
        String filename = "./target/test-classes/sotacsv/M0NOM_567534_activator_20210928.csv";

        SotaCsvFileReader reader = new SotaCsvFileReader();
        Adif3 log = reader.read(filename, StandardCharsets.UTF_8.name(), false);
        assertNotNull(log);
        assertEquals(log.getRecords().size(), 145);
    }
}
