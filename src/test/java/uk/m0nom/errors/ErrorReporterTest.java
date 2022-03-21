package uk.m0nom.errors;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ErrorReporterTest {
    @Test
    public void testThreeArgErrorFormatting() {
        Object[] args = new String[]{"13m", "M0NOM/P", "12:12"};

        String message = ErrorReporter.formatError("uk.m0nom.adif3.transform.comment.parsers.BandRxFieldParser", "parseError", args);
        String expected = "Couldn't parse BandRx field 13m for call M0NOM/P at 12:12, please check, leaving it unmapped";
        assertThat(message).isEqualTo(expected);
    }
}
