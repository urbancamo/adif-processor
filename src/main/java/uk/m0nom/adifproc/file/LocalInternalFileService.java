package uk.m0nom.adifproc.file;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Profile("dev")
@Service
public class LocalInternalFileService implements InternalFileService {

    private final String rootPath;

    public LocalInternalFileService() {
        Path resourceDirectory = Paths.get("src","main","resources");
        rootPath = resourceDirectory.toFile().getAbsolutePath();
    }

    @Override
    public String readFile(String folder, String file) {
        Path filePath = Paths.get(rootPath, folder, file);
        try {
            return Files.readString(filePath);
        } catch (IOException e) {
            return null;
        }
    }
}
