package uk.m0nom.adif3;

import org.junit.Test;

public class BadGridTest
{
    @Test
    public void testApp() {
        String cli = "-k --encoding windows-1251 --qrz-username M0NOM --qrz-password WindermereIsMyQTH -md -o ../ ../../src/test/resources/adif/2021-08-23-ADIF-with-bad-gridsquare.adi";
        String[] args = cli.split(" ");

        FileTransformerApp app = new FileTransformerApp(args);
        app.run();
    }
}
