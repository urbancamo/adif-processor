package uk.m0nom.adif3;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

public class FileTransformerAppTest
{
    @Test
    public void testApp() {
        String args[] = new String[2];
        args[0] = "../test-classes/adif/2021-06-13-Lambrigg-Fell-SOTA.adi";
        args[1] = "../2021-06-13-Lambrigg-Fell-SOTA-fta.adi";

        //System.out.println(new File(".").getAbsolutePath());
        FileTransformerApp app = new FileTransformerApp(args);
        app.run();
    }
}
