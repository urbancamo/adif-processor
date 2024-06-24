package uk.m0nom.adifproc.file;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.s3.model.S3Object;
import lombok.Getter;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Service
@Getter
public class AwsS3FileUtils {
    private AmazonS3 s3client = null;
    private final static String ADIF_PROC_BUCKET = "adif-processor";
    private final boolean configured;

    private static final Logger logger = Logger.getLogger(AwsS3FileUtils.class.getName());

    public AwsS3FileUtils(Environment env) {
        // These keys should be defined in the application environment as variables.
        // They should be those provided for the user 'adif_processor' in AWS IAM.
        // see AWS IAM User -> Access Keys.
        // the 'adif_processor' user has permissions AmazonS3FullAccess originating from 'adif-s3-group'
        String accessKey = env.getProperty("AWS_ACCESS_KEY");
        String secretKey = env.getProperty("AWS_SECRET_KEY");

        configured = StringUtils.isNotEmpty(accessKey) && StringUtils.isNotEmpty(secretKey);
        if (configured) {
            AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
            s3client = AmazonS3ClientBuilder.standard().withCredentials(new AWSStaticCredentialsProvider(credentials)).withRegion(Regions.EU_WEST_2).build();
            logger.info("AWS S3 file utilities configured");
        }
    }

    public String readFile(String folder, String inputFile) {
        if (isConfigured()) {
            S3Object s3object = s3client.getObject(ADIF_PROC_BUCKET, String.format("%s/%s", folder, inputFile));
            StringWriter writer = new StringWriter();
            try {
                // copy input stream to writer
                IOUtils.copy(s3object.getObjectContent(), writer, StandardCharsets.UTF_8);
            } catch (IOException ioe) {
                logger.severe(String.format("Error reading %s from %s in S3 bucket %s: %s", inputFile, folder, ADIF_PROC_BUCKET, ioe.getMessage()));
            }
            return writer.toString();
        }
        return null;
    }
}
