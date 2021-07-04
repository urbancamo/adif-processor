package uk.m0nom.adif3;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

public class FileTransformerAppTest
{
    @Test
    public void testApp() {
        String cli = "-k --encoding windows-1251 --qrz --qrz-username M0NOM --qrz-password WindermereIsMyQTH -g IO84mj91mb ../../src/test/resources/adif/2019-12-31-Queen-Adelaide-Hill.adi";
        String args[] = cli.split(" ");

        FileTransformerApp app = new FileTransformerApp(args);
        app.run();
    }
}
