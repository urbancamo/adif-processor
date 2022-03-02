package uk.m0nom.adif3;

import org.junit.Test;

public class FileTransformerAppTest
{
    @Test
    public void testApp() {
        String cli = "-k --encoding windows-1251 --qrz-username M0NOM --qrz-password WindermereIsMyQTH -l IO84MJ91MB -md -o ../ ./target/test-classes/adif/2021-07-08-Queen-Adelaides-Hill.adi";
        String[] args = cli.split(" ");

        FileTransformerApp app = new FileTransformerApp(args);
        app.run();
    }
}
