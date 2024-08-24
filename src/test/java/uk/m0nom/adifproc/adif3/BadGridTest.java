package uk.m0nom.adifproc.adif3;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import uk.m0nom.adifproc.FileProcessorApplication;

@SpringBootTest
@ActiveProfiles("test")
public class BadGridTest
{
    @Autowired
    private FileProcessorApplication app;

    private static final String[] args = ("-k --encoding windows-1251 -md -o ../ ./target/test-classes/adif/2021-08-23-ADIF-with-bad-gridsquare.adi").split(" ");

    @BeforeEach
    public void setup() {
        app.setArgs(args);
    }

    @Test
    public void testApp() {
        try {
            app.internalRun();
        } catch (Exception e) {
            Assertions.fail("testData() threw exception while setting up the test. ", e);
        }
    }
}
