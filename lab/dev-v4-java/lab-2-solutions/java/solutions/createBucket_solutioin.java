package dev.labs.s3;

import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.awscore.exception.AwsServiceException;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketRequest;
import software.amazon.awssdk.services.s3.model.HeadBucketResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.waiters.S3Waiter;

public class createBucket {

    static getInputs config = new getInputs();

    public static void main(String[] args) throws Exception {

        // Read inputs - bucket name, lab region
        String bucketName = config.getBucketName();
        Region labRegion = Region.of(config.getLabRegion());

        /*
        ////////////////////////////////
                   TODO 1
          Create S3 service client
        ///////////////////////////////
        */
        
        S3Client s3 = S3Client.builder()
                .region(labRegion)
                .build();

        // Check if bucket doesn't already exist using HeadBucket
        if (!bucketExisting(s3, bucketName)) {
            createBucket(s3, bucketName);   // Create bucket
        }

        s3.close();  //Close S3 client

    }

    public static boolean bucketExisting(S3Client s3, String bucketName) {
        boolean check = true;
        System.out.println("Head Bucket operation... ");
        try {
            /*
        //////////////////////////////////////////////////////////////////////////////////
                   TODO 2
         Create HeadBucket object to determine if bucket exists and you have permissions
        /////////////////////////////////////////////////////////////////////////////////
        */
            HeadBucketRequest request = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            HeadBucketResponse result = s3.headBucket(request);

            if (result.sdkHttpResponse().statusCode() == 200) {
                System.out.println("    Bucket is already existing! ");
            }
        }
        catch (AwsServiceException awsEx) {
            switch (awsEx.statusCode()) {
                case 404:
                    System.out.println("    No such bucket existing.");
                    check = false;
                    break;
                case 400 :
                    System.out.println("    Indicates that you are trying to access a bucket from a different Region than where the bucket exists.");break;
                case 403 :
                    System.out.println("    Permission errors in accessing bucket.");break;
            }
        }
        return check;
    }

    public static void createBucket(S3Client s3Client, String bucketName) {

        System.out.format("\nCreating bucket: %s\n\n", bucketName);

        try {
        /*
        //////////////////////////////////////////////////////////////////////////////////
                                       TODO 3
                            Create a S3 waiter objects
        /////////////////////////////////////////////////////////////////////////////////
        */
            S3Waiter s3Waiter = s3Client.waiter();

        /*
        //////////////////////////////////////////////////////////////////////////////////
                                       TODO 4
                            Build request to CreateBucket
        /////////////////////////////////////////////////////////////////////////////////
        */
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            // Create bucket using request
            s3Client.createBucket(bucketRequest);

            HeadBucketRequest bucketRequestWait = HeadBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            System.out.format("Waiting on ");
            // Wait until the bucket is created and print out the response
            WaiterResponse<HeadBucketResponse> waiterResponse = s3Waiter.waitUntilBucketExists(bucketRequestWait);
            waiterResponse.matched().response().ifPresent(System.out::println);
            System.out.format("    Bucket \"%s\" is ready\n",bucketName);

        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}