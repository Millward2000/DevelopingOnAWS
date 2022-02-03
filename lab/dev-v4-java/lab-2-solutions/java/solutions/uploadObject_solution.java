package dev.labs.s3;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class uploadObject {

    static getInputs config = new getInputs();

    public static void main(String[] args) throws Exception {

        // Read inputs - bucket name, lab region
        String bucketName = config.getBucketName();
        Region labRegion = Region.of(config.getLabRegion());
        String objectKey = config.getObjectName();
        String filePath = config.getFile();

        // Create S3 service client
        S3Client s3 = S3Client.builder()
                .region(labRegion)
                .build();

        // Upload a file with initial notes
        putS3Object(s3, bucketName, objectKey, filePath);

        s3.close(); //Close S3 service client
    }

    private static void putS3Object(S3Client s3, String bucketName, String objectKey, String filePath) {
        try {

        /*
        //////////////////////////////////////////////////////////////////////////////////
                                       TODO 5 
                            Assign custom metadata tag
        /////////////////////////////////////////////////////////////////////////////////
        */
            Map<String, String> metadata = new HashMap<>();
            metadata.put("x-amz-meta-myVal2", "lab testing");
        

        /*
        //////////////////////////////////////////////////////////////////////////////////
                                       TODO 6 
                   Build request using bucket name, key, and metadata
        /////////////////////////////////////////////////////////////////////////////////
        */

            PutObjectRequest putObject = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .metadata(metadata)
                    .build();

            System.out.format("\n Uploading file from \"%s\"", new File(filePath).getAbsolutePath());

            // Run request using source file path to be uploaded
            PutObjectResponse response = s3.putObject(putObject,
                    RequestBody.fromFile(Paths.get(filePath)));

            System.out.format("\n\n Upload completed.\n     Tag: %s \n", response.eTag());

        } catch (S3Exception e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
}