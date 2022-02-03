package dev.labs.s3;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class getInputs {

    public String getBucketName() throws Exception {
        String bucketName = readProperties().getProperty("bucket_name");
         return bucketName;
    }
    public String getLabRegion() throws Exception {
        String labRegion = readProperties().getProperty("lab_region");
        return labRegion;
    }
    public String getFile() throws Exception {
        String file = readProperties().getProperty("file");
        return file;
    }
    public String getObjectName() throws Exception {
        String objectName = readProperties().getProperty("object_name");
        return objectName;
    }
        public String getNewObjectName() throws Exception {
        String newObjectName = readProperties().getProperty("new_object_name");
        return newObjectName;
    }
    public static Properties readProperties() throws Exception {
        InputStream configFile = getInputs.class.getClassLoader().getResourceAsStream("config.properties");
        Properties properties = new Properties();
        try { properties.load(configFile); }
        catch (FileNotFoundException fnfe) { fnfe.printStackTrace(); }
        catch (IOException ioe) { ioe.printStackTrace(); }
        return properties;
    }

}
