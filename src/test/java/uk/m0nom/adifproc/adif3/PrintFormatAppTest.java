package uk.m0nom.adifproc.adif3;

import org.junit.jupiter.api.Test;
import uk.m0nom.adifproc.adif3.cli.PrintFormatApp;

public class PrintFormatAppTest
{
    @Test
    public void testApp() {
        String[] args = new String[2];
        args[0] = "target/test-classes/adif/2021-03-02-Gummers-How.adi";
        args[1] = "src/main/resources/adif-printer-132-md.yaml";

        //System.out.println(new File(".").getAbsolutePath());
        PrintFormatApp app = new PrintFormatApp(args);
        app.run();
    }
}
