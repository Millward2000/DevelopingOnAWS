package dev.labs.dynamodb;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class getInputs {

    public static Properties readProperties() throws Exception {
        InputStream configFile = getInputs.class.getClassLoader().getResourceAsStream("config.properties");
        Properties properties = new Properties();
        try {
            properties.load(configFile);
        } catch (FileNotFoundException fnfe) {
            fnfe.printStackTrace();
        } catch (IOException ioe) {
            ioe.printStackTrace();
        }
        return properties;
    }

    public String getTableName() throws Exception {
        String bucketName = readProperties().getProperty("table_name");
        return bucketName;
    }

    public String getLabRegion() throws Exception {
        String labRegion = readProperties().getProperty("lab_region");
        return labRegion;
    }

    public String getPrimaryKey() throws Exception {
        String file = readProperties().getProperty("primary_key");
        return file;
    }

    public String getSortKey() throws Exception {
        String objectName = readProperties().getProperty("sort_key");
        return objectName;
    }

    public String getRCU() throws Exception {
        String objectName = readProperties().getProperty("rcu");
        return objectName;
    }

    public String getWCU() throws Exception {
        String objectName = readProperties().getProperty("wcu");
        return objectName;
    }

    public String getQueryUser() throws Exception {
        String objectName = readProperties().getProperty("user_id");
        return objectName;
    }

    public String getQueryNote() throws Exception {
        String objectName = readProperties().getProperty("note_id");
        return objectName;
    }

    public String getSearchText() throws Exception {
        String objectName = readProperties().getProperty("search_text");
        return objectName;
    }

    public String getNewNote() throws Exception {
        String objectName = readProperties().getProperty("new_note");
        return objectName;
    }

}
