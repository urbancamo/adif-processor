package uk.m0nom.adifproc.file;

import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

@Profile("dev")
@Service
public class LocalInternalFileService implements InternalFileService {

    private final String rootPath;

    public LocalInternalFileService() {
        Path resourceDirectory = Paths.get("src","main","resources");
        rootPath = resourceDirectory.toFile().getAbsolutePath();
    }
    @Override
    public Set<String> getFiles(String dir) {
        Path filePath = Paths.get(rootPath, dir);
        Set<String> fileList = new HashSet<>();
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(filePath)) {
            for (Path path : stream) {
                if (!Files.isDirectory(path)) {
                    fileList.add(path.getFileName().toString());
                }
            }
        } catch (Exception e) {
            // TODO
        }
        return fileList;
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
