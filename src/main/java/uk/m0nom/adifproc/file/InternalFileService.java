package uk.m0nom.adifproc.file;

import org.apache.commons.io.IOUtils;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Set;
import java.util.logging.Logger;

@Service
public class InternalFileService {
    private static final Logger logger = Logger.getLogger(InternalFileService.class.getName());

    private final AwsS3FileUtils awsS3FileUtils;

    public InternalFileService(AwsS3FileUtils awsS3FileUtils) {
        this.awsS3FileUtils = awsS3FileUtils;
    }

    public Set<String> getFiles(String filePath) {
        return awsS3FileUtils.getFiles(filePath);
    }

    public String readFile(String folder, String filePath) {
        return awsS3FileUtils.readFile(folder, filePath);
    }

    public void archiveData(String folder, String filename, String content) {
        // Read content of file
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
}
