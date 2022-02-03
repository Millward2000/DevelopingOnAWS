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

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.*;
import com.amazonaws.services.dynamodbv2.model.AmazonDynamoDBException;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ExpectedAttributeValue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class notesCRUDmapperSolution {

    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard()
            .build();


    public static void main(String[] args) throws IOException {

        // TODO 1 BEGIN
        // Define a DynamoDB mapper to associate to the instance of NotesItem class
        DynamoDBMapper mapper = new DynamoDBMapper(client);
        // TODO 1 END

        testCRUDOperations(mapper);
        System.out.println("CRUD operations complete!");

        testPutWithConditionalUpdate(mapper);
    }

    private static void testCRUDOperations(DynamoDBMapper mapper) {

        // TODO 6 BEGIN
        // Instantiate NotesItem class to maps to the DynamoDB table. Definition of the class is partially provided to you
        NotesItems item = new NotesItems();
        // TODO 6 END

        // TODO 7 BEGIN
        // Set attribute values to UserId, NoteId, and Note using class methods
        item.setUserId("testuser");
        item.setNoteId(1);
        item.setNotes("this is my very first note");
        // TODO 7 END

        try {
            // TODO 8 BEGIN
            // Save the item (note) to Note Table.
            mapper.save(item);
            // TODO 8 END


            // TODO 9 BEGIN
            // Retrieve the item from Notes
            NotesItems itemRetrieved = mapper.load(NotesItems.class, "testuser", 1);
            System.out.println("Item retrieved:");
            System.out.println(itemRetrieved);
            // TODO 9 END

            // TODO 10 BEGIN
            // Update the item in Notes
            itemRetrieved.setNotes("updated notes");
            // Save the updated attributes
            mapper.save(itemRetrieved);
            // TODO 10 END

            System.out.println("Item updated:");
            System.out.println(itemRetrieved);

            // Retrieve the updated item.
            //Change mapper configuration to set read consistency
            DynamoDBMapperConfig config = DynamoDBMapperConfig.builder()
                    .withConsistentReads(DynamoDBMapperConfig.ConsistentReads.CONSISTENT)
                    .build();

            NotesItems updatedItem = mapper.load(NotesItems.class, "testuser", 1, config);
            System.out.println("Retrieved the previously updated item:");
            System.out.println(updatedItem);

            // TODO 11 BEGIN
            // Delete the item from Notes
            NotesItems deleteItem = mapper.load(NotesItems.class, "testuser", 1, config);
            mapper.delete(deleteItem);
            // TODO 11 END

            System.out.println("deleting the previously existing item:");
            System.out.println(deleteItem);

            // Try to retrieve deleted item.
            NotesItems deletedItem = mapper.load(NotesItems.class, deleteItem.getUserId(), deleteItem.getNoteId(), config);
            if (deletedItem == null) {
                System.out.println("Done - Sample item is deleted.");
            }
        } catch (AmazonDynamoDBException e) {
            System.err.println(e.getMessage());
            e.getStackTrace();
        } catch (DynamoDBMappingException ddbme) {
            System.err.println("Client side error in Mapper, fix before retrying. Error: " + ddbme.getMessage());
        } catch (Exception ex) {
            System.err.println("An exception occurred, investigate and configure retry strategy. Error: " + ex.getMessage());
        }
    }

    private static void testPutWithConditionalUpdate(DynamoDBMapper mapper) {

        NotesItems item = new NotesItems();

        // Retrieve the item to update
        NotesItems itemRetrieved = mapper.load(NotesItems.class, "newbie", 1);
        System.out.println("Item retrieved:");
        System.out.println(itemRetrieved);

        //Set new values
        item.setUserId(itemRetrieved.getUserId());
        item.setNoteId(itemRetrieved.getNoteId());
        item.setNotes("free swag registration code " + ThreadLocalRandom.current().nextInt());

        ArrayList<AttributeValue> list = new ArrayList<AttributeValue>();
        list.add(new AttributeValue().withS(itemRetrieved.getNotes()));

        Map<String, ExpectedAttributeValue> expectedAttributes = new HashMap<String, ExpectedAttributeValue>();

        ExpectedAttributeValue expectedAttributeValue = new ExpectedAttributeValue()
                .withAttributeValueList(list)
                .withComparisonOperator("EQ");

        expectedAttributes.put("Note", expectedAttributeValue);
        // TODO 12  BEGIN
        // Update item using withExpected expression
        mapper.save(item, new DynamoDBSaveExpression().withExpected(expectedAttributes));
        // TODO 12 END

        NotesItems itemUpdated = mapper.load(NotesItems.class, "newbie", 1);
        System.out.println("Item updated with new notes:");
        System.out.println(itemUpdated);
    }

    // TODO 2 BEGIN
    // Define DynamoDB Table annotation to maps NotesItems class to DynamoDB table name Notes
    @DynamoDBTable(tableName = "Notes")
    // TODO 2 END
    public static class NotesItems {

        //Set up Data Members that correspond to columns in the Music table
        private String UserId;
        private Integer NoteId;
        private String Note;

        // TODO 3 BEGIN
        // Define the primary key annotation on the attribute UserId
        @DynamoDBHashKey(attributeName = "UserId")
        // TODO 3 END
        public String getUserId() {
            return this.UserId;
        }

        public void setUserId(String UserId) {
            this.UserId = UserId;
        }

        // TODO 4 BEGIN
        // Define the sort key annotation on the attribute NoteId
        @DynamoDBRangeKey(attributeName = "NoteId")
        // TODO 4 END
        public Integer getNoteId() {
            return this.NoteId;
        }

        public void setNoteId(Integer NoteId) {
            this.NoteId = NoteId;
        }

        // TODO 5 BEGIN
        // Define an optional attribute annotation for Note
        @DynamoDBAttribute(attributeName = "Note")
        // TODO 5 END
        public String getNotes() {
            return this.Note;
        }

        public void setNotes(String Note) {
            this.Note = Note;
        }

        @Override
        public String toString() {
            return "Notes [User=" + UserId + ", Note Id=" + NoteId + ", Notes=" + Note + "]";
        }
    }
}
