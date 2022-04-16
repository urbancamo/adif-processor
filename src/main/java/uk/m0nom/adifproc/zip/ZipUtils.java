package uk.m0nom.adifproc.zip;

import org.apache.commons.io.FilenameUtils;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
    public static String compress(String sourcePath, String destExtension) throws IOException {
        String outPath = String.format("%s%s.%s", FilenameUtils.getFullPath(sourcePath), FilenameUtils.getBaseName(sourcePath), destExtension);
        try (
                FileInputStream fis = new FileInputStream(sourcePath);
                FileOutputStream fos = new FileOutputStream(outPath);
                ZipOutputStream zipOut = new ZipOutputStream(fos)) {
            ZipEntry zipEntry = new ZipEntry(FilenameUtils.getName(sourcePath));
            zipOut.putNextEntry(zipEntry);
            byte[] bytes = new byte[1024];
            int length;
            while ((length = fis.read(bytes)) >= 0) {
                zipOut.write(bytes, 0, length);
            }
        }
        return outPath;
    }
}