package uk.m0nom.adifproc.file;

import java.util.Set;

public interface InternalFileService {

    Set<String> getFiles(String filePath);

    String readFile(String folder, String filePath);

}
