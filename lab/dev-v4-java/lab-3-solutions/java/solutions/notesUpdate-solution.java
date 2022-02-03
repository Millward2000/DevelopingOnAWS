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
import com.amazonaws.services.dynamodbv2.document.DynamoDB;
import com.amazonaws.services.dynamodbv2.document.Table;
import com.amazonaws.services.dynamodbv2.document.UpdateItemOutcome;
import com.amazonaws.services.dynamodbv2.document.spec.UpdateItemSpec;
import com.amazonaws.services.dynamodbv2.document.utils.NameMap;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;
import com.amazonaws.services.dynamodbv2.model.ConditionalCheckFailedException;
import com.amazonaws.services.dynamodbv2.model.ReturnValue;

public class notesUpdate {

    //Create DynamoDB client and use Document API wrapper
    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    static DynamoDB dynamoDB = new DynamoDB(client);
    static getInputs config = new getInputs();

    public static void main(String[] args) throws Exception {

        //Initialize the Query strings
        String qUserId = config.getQueryUser();
        Integer qNoteId = Integer.valueOf(config.getQueryNote());
        String newNote = config.getNewNote();

        //Get Notes table information
        Table table = dynamoDB.getTable(config.getTableName());

        //Add a new attribute Is_Incomplete for a note item
        addNewAttribute(table, qUserId, qNoteId);

        // TODO 10 BEGIN
        //Allow update to the Notes item only if the note is incomplete - SUCCESS
        updateExistingAttributeConditionally(table, qUserId, qNoteId, newNote);

        //Allow update to the Notes item only if the note is incomplete - FAILURE
        updateExistingAttributeConditionally(table, qUserId, qNoteId, newNote);
        // TODO 10 END
    }

    private static void addNewAttribute(Table table, String userId, Integer noteId) {

        //Build request: Update specification to add Is_Incomplete attribute and assign value "Yes" for matching KEYs
        //TODO 7 BEGIN
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
        .withPrimaryKey("UserId", userId, "NoteId", noteId)
        .withUpdateExpression("set #inc = :val1")
        .withNameMap(new NameMap()
                .with("#inc", "Is_Incomplete"))
        .withValueMap(new ValueMap()
                .withString(":val1", "Yes"))
        .withReturnValues(ReturnValue.ALL_NEW);

        //TODO 7 END
        try {
            //Run update and retrieve response
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);

            // Check the response using getItem
            System.out.println("UPDATE#1: Printing item after adding the new attribute \"Is_Incomplete\" :");
            System.out.println(outcome.getItem().toJSONPretty());
        } catch (Exception e) {
            System.err.println("Error updating item in " + table.getTableName());
            System.err.println(e.getMessage());
        }
    }

    private static void updateExistingAttributeConditionally(Table table, String userId, Integer noteId, String newNote) {

        //Build request: Update specification to update attributes "Note" and "Is_Incomplete" only when assign valued "Yes" for matching KEYs
        UpdateItemSpec updateItemSpec = new UpdateItemSpec()
                .withPrimaryKey("UserId", userId, "NoteId", noteId)
                .withUpdateExpression("set Note = :v_notes, Is_Incomplete = :v_new")
                // TODO 8 BEGIN
                .withConditionExpression("Is_Incomplete = :v_old")
                // TODo 8 END
                .withValueMap(new ValueMap()
                .withString(":v_notes", newNote)
                .withString(":v_new", "No")
                .withString(":v_old", "Yes"))
                .withReturnValues(ReturnValue.UPDATED_NEW);

        try {
            //Run update and retrieve response
            UpdateItemOutcome outcome = table.updateItem(updateItemSpec);

            // Check the response using getItem
            System.out.println("\nUPDATE#2: Printing item after the conditional update for the item - \"" + userId + "\" and \"" + noteId + "\"  - SUCCESS:");
            System.out.println(outcome.getItem().toJSONPretty());
        }
        //TODO 9 BEGIN
        catch (ConditionalCheckFailedException e) {
            System.out.println("\nUPDATE#2 - REPEAT: Printing item after the conditional update for the item - \"" + userId + "\" and \"" + noteId + "\"  - FAILURE:");
            System.out.println("UpdateItem failed on item due to unmatching condition!");
            System.err.println(e.getMessage());
        }
        // TODO 9 END
        catch (Exception e) {
            System.err.println("Error updating item in " + table.getTableName());
            System.err.println(e.getMessage());
        }
    }
}


