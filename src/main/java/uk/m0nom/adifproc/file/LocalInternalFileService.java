package uk.m0nom.adifproc.file;

import org.joda.time.Instant;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.chrono.ChronoLocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

@Profile("dev")
@Service
public class LocalInternalFileService implements InternalFileService {

    private final String rootPath;

    private static final Logger logger = Logger.getLogger(LocalInternalFileService.class.getName());

    public LocalInternalFileService() {
        Path resourceDirectory = Paths.get("src","main","resources");
        rootPath = resourceDirectory.toFile().getAbsolutePath();
    }

    public String getLogFilePath() {
        return "/tmp/user-access.log";
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

    public void logUserAccess(String usernames) {
        try (FileWriter fw = new FileWriter(getLogFilePath(), true)) {
            String timestamp = LocalDateTime.now().toString();
            fw.write(String.format("%s: %s", timestamp, usernames) + System.lineSeparator());
        } catch (IOException e) {
            logger.warning(String.format("Error writing to user log %s: %s", getLogFilePath(), e.getMessage()));
        }
    }
}
