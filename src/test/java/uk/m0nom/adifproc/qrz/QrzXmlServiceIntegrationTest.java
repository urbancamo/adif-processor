package uk.m0nom.adifproc.qrz;


import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test the QrzXmlService is able to look up data on a callsign
 * NOTE: you will need to plug in your username and password for this to work correctly
 * (which is why the default this test is ignored)
 */
public class QrzXmlServiceIntegrationTest {
    private final static String USERNAME = "";
    private final static String PASSWORD = "";

    @Test
    public void testCallSignLookup() {
        if (StringUtils.isNotEmpty(USERNAME)) {
            QrzXmlService service = new QrzXmlService();
            service.setCredentials(USERNAME, PASSWORD);
            assertThat(service.refreshSessionKey()).isNotNull();

            // Now have a session key
            QrzCallsign callsign = service.getCallsignData("M0NOM");
            assertThat(callsign).isNotNull();
        }
    }
}
