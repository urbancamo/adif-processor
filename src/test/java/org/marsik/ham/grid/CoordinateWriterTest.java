package org.marsik.ham.grid;

import org.junit.Test;
import org.marsik.ham.grid.CoordinateWriter;

import static org.assertj.core.api.Assertions.assertThat;

public class CoordinateWriterTest {
    @Test
    public void testNorthLatitude() throws Exception {
        assertThat(org.marsik.ham.grid.CoordinateWriter.latToDM(50.5))
                .isNotNull()
                .isEqualTo("N050 30.000");
    }

    @Test
    public void testEastLongitude() throws Exception {
        assertThat(org.marsik.ham.grid.CoordinateWriter.lonToDM(15.2))
                .isNotNull()
                .isEqualTo("E015 11.999");
    }

    @Test
    public void testLatitudePadding() throws Exception {
        assertThat(org.marsik.ham.grid.CoordinateWriter.latToDM(50.07))
                .isNotNull()
                .isEqualTo("N050 04.200");
    }

    @Test
    public void testLongitudePadding() throws Exception {
        assertThat(org.marsik.ham.grid.CoordinateWriter.lonToDM(15.01))
                .isNotNull()
                .isEqualTo("E015 00.599");
    }

    @Test
    public void testLongitudePadding2() throws Exception {
        assertThat(org.marsik.ham.grid.CoordinateWriter.lonToDM(15.98765432))
                .isNotNull()
                .isEqualTo("E015 59.259");
    }
    @Test
    public void testParsingNorthLatitude() throws Exception {
        assertThat(org.marsik.ham.grid.CoordinateWriter.dmToLat("N050 30.000"))
                .isEqualTo(50.5);
    }

    @Test
    public void testParsingEastLongitude() throws Exception {
        assertThat(org.marsik.ham.grid.CoordinateWriter.dmToLon("E015 30.000"))
                .isEqualTo(15.5);
    }

    @Test
    public void testParsingEmptySoutherly() throws Exception {
        assertThat(CoordinateWriter.dmToLat("S000 00.000"))
                .isEqualTo(-0.0);
    }

}
