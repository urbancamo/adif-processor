package uk.m0nom.adifproc.zip;


import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;


public class ZipUtilsTest {
    @Test
    public void testZipFile() {
        File zipFile = null;
        File destFile = null;
        try {
            String inPathname = "./src/test/resources/kml/";
            String filename = "1650041557025-out-SAT_log_20220403_1823.kml";

            //System.out.printf("Current working directory: %s%n", new File(".").getAbsolutePath());

            String tmpDir = System.getProperty("java.io.tmpdir");

            File sourceFile = new File(inPathname + File.separator + filename);
            destFile = new File(tmpDir + File.separator + filename);

            // Copy file to tmp directory
            FileUtils.copyFile(sourceFile, destFile);

            String zipFilename = ZipUtils.compress(destFile.getAbsolutePath(), "kmz");
            zipFile = new File(zipFilename);
            assertThat(zipFile.exists()).isTrue();
            assertThat(zipFile.isFile()).isTrue();

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            // Cleanup tmp files
            if (destFile != null && destFile.exists() && destFile.isFile()) {
                assertThat(destFile.delete()).isTrue();
            }
            // Cleanup tmp files
            if (zipFile != null && zipFile.exists() && zipFile.isFile()) {
                assertThat(zipFile.delete()).isTrue();
            }
        }
    }
}
