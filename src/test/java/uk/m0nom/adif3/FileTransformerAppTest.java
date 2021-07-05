package uk.m0nom.adif3;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

public class FileTransformerAppTest
{
    @Test
    public void testApp() {
        String cli = "-k --encoding windows-1251 --qrz --qrz-username M0NOM --qrz-password WindermereIsMyQTH -g EM75jr --kml-s2s --pota K-5490 -o ../ ../../src/test/resources/adif/WW4N@POTA-K-5490-20210704.adi";
        String[] args = cli.split(" ");

        FileTransformerApp app = new FileTransformerApp(args);
        app.run();
    }
}
