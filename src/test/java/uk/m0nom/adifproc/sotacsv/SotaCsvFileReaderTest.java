package uk.m0nom.adifproc.sotacsv;

import org.junit.jupiter.api.Test;
import org.marsik.ham.adif.Adif3;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

public class SotaCsvFileReaderTest {

    @Test
    public void testSotaCsvReader() throws IOException {
        String filename = "./target/test-classes/sotacsv/M0NOM_567534_activator_20210928.csv";

        SotaCsvFileReader reader = new SotaCsvFileReader();
        Adif3 log = reader.read(filename, StandardCharsets.UTF_8.name(), false);
        assertThat(log).isNotNull();
        assertThat(log.getRecords().size()).isEqualTo(145);
    }
}
