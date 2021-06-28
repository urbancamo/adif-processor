package uk.m0nom.qrz;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

/**
 * Test the QrzXmlService is able to look up data on a callsign
 * NOTE: you will need to plug in your username and password for this to work correctly
 * (which is why be default this test is ignored)
 */
public class QrzXmlServiceIntegrationTest {
    private final static String USERNAME = "";
    private final static String PASSWORD = "";

    @Ignore
    @Test
    public void testCallSignLookup() {
        QrzXmlService service = new QrzXmlService(USERNAME, PASSWORD);
        Assert.assertTrue(service.getSessionKey());

        // Now have a session key
        QrzCallsign callsign = service.getCallsignData("M0NOM");
        Assert.assertNotNull(callsign);
    }
}
