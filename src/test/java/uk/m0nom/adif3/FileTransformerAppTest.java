package uk.m0nom.adif3;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

public class FileTransformerAppTest
{
    @Test
    public void testApp() {
        String cli = "-k --encoding windows-1251 -ks2s --latitude '54.4055' --longitude '-3.01796' ../../src/test/resources/adif/2019-08-31-Black-Fell.adi";
        String args[] = cli.split(" ");

        FileTransformerApp app = new FileTransformerApp(args);
        app.run();
    }
}
