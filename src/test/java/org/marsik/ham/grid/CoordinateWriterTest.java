package org.marsik.ham.grid;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class CoordinateWriterTest {
    @Test
    public void testNorthLatitude() {
        assertThat(org.marsik.ham.grid.CoordinateWriter.latToDM(50.5))
                .isNotNull()
                .isEqualTo("N050 30.000");
    }

    @Test
    public void testEastLongitude() {
        assertThat(org.marsik.ham.grid.CoordinateWriter.lonToDM(15.2))
                .isNotNull()
                .isEqualTo("E015 11.999");
    }

    @Test
    public void testLatitudePadding() {
        assertThat(org.marsik.ham.grid.CoordinateWriter.latToDM(50.07))
                .isNotNull()
                .isEqualTo("N050 04.200");
    }

    @Test
    public void testLongitudePadding() {
        assertThat(org.marsik.ham.grid.CoordinateWriter.lonToDM(15.01))
                .isNotNull()
                .isEqualTo("E015 00.599");
    }

    @Test
    public void testLongitudePadding2() {
        assertThat(org.marsik.ham.grid.CoordinateWriter.lonToDM(15.98765432))
                .isNotNull()
                .isEqualTo("E015 59.259");
    }
    @Test
    public void testParsingNorthLatitude() {
        assertThat(org.marsik.ham.grid.CoordinateWriter.dmToLat("N050 30.000"))
                .isEqualTo(50.5);
    }

    @Test
    public void testParsingEastLongitude() {
        assertThat(org.marsik.ham.grid.CoordinateWriter.dmToLon("E015 30.000"))
                .isEqualTo(15.5);
    }

    @Test
    public void testParsingEmptySoutherly() {
        assertThat(CoordinateWriter.dmToLat("S000 00.000"))
                .isEqualTo(-0.0);
    }

}
