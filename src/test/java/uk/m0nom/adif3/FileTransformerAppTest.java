package uk.m0nom.adif3;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

public class FileTransformerAppTest
{
    @Test
    public void testApp() {
        String args[] = new String[6];
        args[0] = "-k";
        args[1] = "--encoding";
        args[2] = "windows-1251";
        args[3] = "--grid";
        args[4] = "IO84MG79LG";
        args[5] = "../../src/test/resources/adif/2021-03-02-Gummers-How-ssb.adi";

        //System.out.println(new File(".").getAbsolutePath());
        FileTransformerApp app = new FileTransformerApp(args);
        app.run();
    }
}
