package dev.labs.s3;

import com.amazonaws.services.s3.model.GetBucketWebsiteConfigurationRequest;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

public class hostS3Website {

    static getInputs config = new getInputs();

    public static void main(String[] args) throws Exception {

        // Read inputs - bucket name, lab region
        String bucketName = config.getBucketName();
        Region labRegion = Region.of(config.getLabRegion());
        String indexPage = "index.html";
        String errorPage = "error.html";

        // Create S3 service client
        S3Client s3 = S3Client.builder()
                .region(labRegion)
                .build();

        // Configure website setting for your bucket
        setWebsiteConfig(s3, bucketName, indexPage, errorPage);

        s3.close(); //Close S3 service client
    }

    private static void setWebsiteConfig(S3Client s3, String bucketName, String indexPage, String errorPage) throws Exception{
        try {

            // Build website configuration using Index and Error pages
            WebsiteConfiguration websiteConfig = WebsiteConfiguration.builder()
                    .indexDocument(IndexDocument.builder().suffix(indexPage).build())
                    .errorDocument(ErrorDocument.builder().key(errorPage).build())
                    .build();

            // Build request using bucket name and website configuration
            PutBucketWebsiteRequest putBucketWebsiteRequest = PutBucketWebsiteRequest.builder()
                    .bucket(bucketName)
                    .websiteConfiguration(websiteConfig)
                    .build();

            // Run request to apply website configurations to your bucket
            s3.putBucketWebsite(putBucketWebsiteRequest);

            System.out.println("Website configuration: ");
            GetBucketWebsiteRequest getBucketWebsiteRequest = GetBucketWebsiteRequest.builder()
                        .bucket(bucketName)
                        .build();
            GetBucketWebsiteResponse getBucketWebsiteResponse = s3.getBucketWebsite(getBucketWebsiteRequest);

            System.out.println("    Index doc: " + getBucketWebsiteResponse.indexDocument());
            System.out.println("    Error doc: " + getBucketWebsiteResponse.errorDocument());
            System.out.println("\nUse the link to access your S3 website after setting up the right permissions:");
            System.out.println("    http://" + bucketName + ".s3-website-" + config.getLabRegion() + ".amazonaws.com");

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}