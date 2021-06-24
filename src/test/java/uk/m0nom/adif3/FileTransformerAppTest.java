package uk.m0nom.adif3;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import java.io.File;

public class FileTransformerAppTest
{
    @Test
    public void testApp() {
        String args[] = new String[5];
        args[0] = "-k";
        args[1] = "--encoding";
        args[2] = "windows-1251";
        args[3] = "-q";
        args[4] = "../../src/test/resources/adif/G8CPZ_log_2021-0624-G_LD037-Little-Mell-Fell.adi";

        //System.out.println(new File(".").getAbsolutePath());
        FileTransformerApp app = new FileTransformerApp(args);
        app.run();
    }
}
