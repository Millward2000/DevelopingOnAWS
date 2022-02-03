/**
 * Copyright 2010-2021 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * <p>
 * This file is licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License. A copy of
 * the License is located at
 * <p>
 * http://aws.amazon.com/apache2.0/
 * <p>
 * This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 * CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package dev.labs.dynamodb;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Item;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.util.Iterator;

public class notesLoadData {

    static getInputs config = new getInputs();

    public static void main(String[] args) throws Exception {

        // Read inputs -  table name
        String tablename = config.getTableName();

        // TODO 0 BEGIN
        //Create DynamoDB client
        AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
                .build();

        //Use the DynamoDB document API wrapper
        DynamoDB dynamoDB = new DynamoDB(client);
        // TODO 0 END
        
        //Use Notes table as resource
        Table table = dynamoDB.getTable(tablename);

        //Input file processing...
        JsonParser parser = new JsonFactory().createParser(new File("notes.json"));
        JsonNode rootNode = new ObjectMapper().readTree(parser);

        //Set an iterator on each JSON note node
        Iterator<JsonNode> iter = rootNode.iterator();

        ObjectNode currentNode;

        System.out.format("\n\n Loading \"%s\" table with data from file \"notes.json\"\n\n", tablename);

        while (iter.hasNext()) {
            currentNode = (ObjectNode) iter.next();

            String userId = currentNode.path("UserId").asText();
            Integer noteId = currentNode.path("NoteId").asInt();
            String note = currentNode.path("Note").asText();

            //Load data into table
            try {
                // TODO 1 BEGIN
                table.putItem(
                        new Item()
                                .withPrimaryKey("UserId", userId, "NoteId", noteId)
                                .withString("Note", note)
                );
                // TODO 1 END
                
                System.out.println("PutItem succeeded: " + userId + " " + noteId + " " + note);
            } catch (AmazonServiceException e) {
                System.err.println(e.getMessage());
            } catch (Exception e) {
                System.err.println("Unable to add movie: " + userId + " " + noteId);
                System.err.println(e.getMessage());
                break;
            }
        }
        parser.close();
    }
}
