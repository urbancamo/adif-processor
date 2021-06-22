package uk.m0nom.qrz;

import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class QrzXmlServiceIntegrationTest {
    @Test
    public void testCallSignLookup() {
        QrzXmlService service = new QrzXmlService();
        Assert.assertTrue(service.getSessionKey());

        // Now have a session key
        QrzCallsign callsign = service.getCallsignData("M0NOM");
        Assert.assertNotNull(callsign);
    }
}
