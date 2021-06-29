package uk.m0nom.adif3;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

public class FileTransformerAppTest
{
    @Test
    public void testApp() {
        String cli = "-k --encoding windows-1251 -ks2s --qrz --qrz-username M0NOM --qrz-password WindermereIsMyQTH --wota LDO-113 ../../src/test/resources/adif/2019-11-21-Brant-Fell-WOTA.adi";
        String args[] = cli.split(" ");

        FileTransformerApp app = new FileTransformerApp(args);
        app.run();
    }
}
