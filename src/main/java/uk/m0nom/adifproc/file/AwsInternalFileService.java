package uk.m0nom.adifproc.file;

import org.springframework.stereotype.Service;

@Service("awsInternalFileService")
public class AwsInternalFileService implements InternalFileService {
    private final AwsS3FileUtils awsS3FileUtils;

    public AwsInternalFileService(AwsS3FileUtils awsS3FileUtils) {
        this.awsS3FileUtils = awsS3FileUtils;
    }

    public String readFile(String folder, String filePath) {
        return awsS3FileUtils.readFile(folder, filePath);
    }
}
