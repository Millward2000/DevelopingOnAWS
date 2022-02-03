package dev.labs.s3;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

public class processObject {

    static getInputs config = new getInputs();

    public static void main(String[] args) throws Exception {

        // Read inputs - bucket name, lab region
        String bucketName = config.getBucketName();
        Region labRegion = Region.of(config.getLabRegion());
        String objectKey = config.getObjectName();
        String newObjectKey = config.getNewObjectName();

        // Create S3 service client
        S3Client s3 = S3Client.builder()
                .region(labRegion)
                .build();

        // Download file notes.csv from the bucket and convert to notes.json
        convertS3Objects(s3, bucketName, objectKey, newObjectKey);

        s3.close(); //Close S3 service client
    }

    private static void convertS3Objects(S3Client s3, String bucketName, String objectKey, String objectNewKey) {
        try {

        // Build request using bucket name and key to download object
        
        // TODO 7 BEGIN
            GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(objectKey)
                    .build();
        // TODO 7 END

            System.out.println("Retrieving notes.txt from bucket...");

            // Get response in bytes
            ResponseBytes<GetObjectResponse> objectBytes = s3.getObjectAsBytes(getObjectRequest);
            byte[] data = objectBytes.asByteArray();

            // PART 2
            //Write to a local file on your disk
            File localFile = new File("localFile.txt");
            OutputStream os = new FileOutputStream(localFile);
            os.write(data);
            os.close();
            System.out.println("    Object downloaded from S3 is written to: " + localFile.getAbsolutePath());

            // PART 3
            // Convert the downloaded notes to JSON format and write to a new file
            System.out.println("\nConverting notes.txt to json format...");
            File convertedFile = new File("notes.json");
            try {
                CsvSchema csv = CsvSchema.emptySchema().withHeader();
                CsvMapper csvMapper = new CsvMapper();
                MappingIterator<Map<?, ?>> mappingIterator = csvMapper.reader().forType(Map.class).with(csv).readValues(localFile);
                List<Map<?, ?>> list = mappingIterator.readAll();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(convertedFile, list);
                System.out.println("    Converted file is written to: " + convertedFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                // PART 4
        
                PutObjectRequest putObject = PutObjectRequest.builder()
                        .bucket(bucketName)
                        .key(objectNewKey)
                        .build();

                System.out.println("\nUploading notes.json file to S3...");

                // Run request using converted file path to be uploaded
                
        PutObjectResponse response = 
        // TODO 8 BEGIN
                s3.putObject(putObject,
                        RequestBody.fromFile(Paths.get(objectNewKey)));
        // TODO 8 END

                System.out.println("    Object uploaded. Tag information:" + response.eTag());
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (S3Exception e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
}