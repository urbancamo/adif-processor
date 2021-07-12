package uk.m0nom.adif3;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

public class FileTransformerAppTest
{
    @Test
    public void testApp() {
        String cli = "-k --encoding windows-1251 --qrz --qrz-username M0NOM --qrz-password WindermereIsMyQTH -md -o ../ ../../src/test/resources/adif/2021-07-08-Queen-Adelaides-Hill.adi";
        String[] args = cli.split(" ");

        FileTransformerApp app = new FileTransformerApp(args);
        app.run();
    }
}
