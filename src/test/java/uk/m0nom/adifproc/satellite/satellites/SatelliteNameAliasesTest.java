package uk.m0nom.adifproc.satellite.satellites;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
public class SatelliteNameAliasesTest {
    @Autowired
    private SatelliteNameAliases aliases;

    @Test
    public void testAliasLoading() {
        String name = aliases.getSatelliteName("ARISS");
        assertThat(name).isEqualTo("ISS");
    }
}
