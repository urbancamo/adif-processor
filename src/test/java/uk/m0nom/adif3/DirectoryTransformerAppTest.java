package uk.m0nom.adif3;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FilenameFilter;

public class DirectoryTransformerAppTest
{
    @Test
    public void testApp() {
        String args[] = new String[1];
        args[0] = "./target/test-classes/adif/";

        cleanup(args[0]);
        DirectoryTransformerApp app = new DirectoryTransformerApp(args);
        app.run();
    }

    private void cleanup(String folderPath) {
        final File folder = new File(folderPath);
        final File[] files = folder.listFiles( new FilenameFilter() {
            @Override
            public boolean accept( final File dir,
                                   final String name ) {
                return name.matches( ".*-fta\\.adi" );
            }
        } );
        for ( final File file : files ) {
            if ( !file.delete() ) {
                System.err.println( "Can't remove " + file.getAbsolutePath() );
            }
        }
    }
}
