package uk.m0nom.adifproc.file;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service("localInternalFileService")
public class LocalInternalFileService implements InternalFileService {

    @Value("${adifproc.files.path}")
    private String relativeFilesFolder;

    @Override
    public String readFile(String folder, String file) {
        Path filePath = Paths.get(relativeFilesFolder, folder, file);
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            return null;
        }
    }
}
