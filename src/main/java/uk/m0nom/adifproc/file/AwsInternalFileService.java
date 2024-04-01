package uk.m0nom.adifproc.file;

import org.apache.commons.io.IOUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Set;
import java.util.logging.Logger;

@Profile("!dev")
@Service
public class AwsInternalFileService implements InternalFileService {
    private static final Logger logger = Logger.getLogger(AwsInternalFileService.class.getName());

    private final AwsS3FileUtils awsS3FileUtils;

    public AwsInternalFileService(AwsS3FileUtils awsS3FileUtils) {
        this.awsS3FileUtils = awsS3FileUtils;
    }

    public String getLogFilePath() {
        return "/var/logs/user-access.log";
    }

    public Set<String> getFiles(String filePath) {
        return awsS3FileUtils.getFiles(filePath);
    }

    public String readFile(String folder, String filePath) {
        return awsS3FileUtils.readFile(folder, filePath);
    }

    public void archiveData(String folder, String filename, String content) {
        awsS3FileUtils.archiveFile(folder, filename, content);
    }

    public void archiveFile(String folder, String filename, String tmpPath, String encoding) {
        String content;
        FileInputStream out = null;
        if (awsS3FileUtils.isConfigured()) {
            // Read content of file
            var filePath = String.format("%s%s", tmpPath, filename);
            try {
                out = new FileInputStream(filePath);
                content = IOUtils.toString(out, encoding);

                // Archive the content into S3 storage
                logger.info(String.format("Archiving output file %s", filename));
                awsS3FileUtils.archiveFile(folder, filename, content);
            } catch (Exception e) {
                logger.severe(e.getMessage());
            } finally {
                try {
                    assert out != null;
                    out.close();
                } catch (IOException e) {
                    logger.severe(e.getMessage());
                }
            }

        }
    }

    public void logUserAccess(String usernames) {
        try (FileWriter fw = new FileWriter(getLogFilePath(), true)) {
            String timestamp = DateTimeFormatter.ISO_ZONED_DATE_TIME.format(new Date().toInstant());
            fw.write(String.format("%s: %s", timestamp, usernames) + System.lineSeparator());
        } catch (IOException e) {
            logger.warning(String.format("Error writing to user log %s: %s", getLogFilePath(), e.getMessage()));
        }
    }
}
