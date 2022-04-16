package uk.m0nom.adifproc.satellite.satellites;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import uk.m0nom.adifproc.FileProcessorApplicationConfig;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = FileProcessorApplicationConfig.class)
public class SatelliteNameAliasesTest {
    @Autowired
    private SatelliteNameAliases aliases;

    @Test
    public void testAliasLoading() {
        String name = aliases.getSatelliteName("ARISS");
        assertThat(name).isEqualTo("ISS");
    }
}
