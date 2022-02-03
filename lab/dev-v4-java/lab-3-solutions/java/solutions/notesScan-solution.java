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
import com.amazonaws.services.dynamodbv2.document.*;
import com.amazonaws.services.dynamodbv2.document.spec.ScanSpec;
import com.amazonaws.services.dynamodbv2.document.utils.ValueMap;

import java.util.Iterator;

public class notesScan {

    //Create DynamoDB client and use Document API wrapper
    static AmazonDynamoDB client = AmazonDynamoDBClientBuilder.standard().build();
    static DynamoDB dynamoDB = new DynamoDB(client);
    static getInputs config = new getInputs();

    public static void main(String[] args) throws Exception {

        //Initialize the Read operations inputs
        String searchText = config.getSearchText();

        //Get Notes table information

        Table table = dynamoDB.getTable(config.getTableName());

        scanAllNotesForTextPaginator(table, searchText);

    }

    private static void scanAllNotesForTextPaginator(Table table, String searchText) {

        //Build request: Scan request with filter expression for a search text in Note

        System.out.format("\n \n Scan table to list items with search text \"%s\" as part of the note:\n", searchText);

        //Build Scan specification with Filter expression, Values, list of attributes to project

        // TODO 4 BEGIN
        ScanSpec scanSpec = new ScanSpec()
                .withFilterExpression("contains (Note, :v_txt)")
                .withValueMap(new ValueMap().withString(":v_txt", searchText))
                .withProjectionExpression("UserId, NoteId, Note");
        // TODO 4 END

        //Limit the response Page size
        // TODO 5 BEGIN
        scanSpec.withMaxPageSize(1);
        // TODO 5 END
        
        //Run scan table using above specifications
        ItemCollection<ScanOutcome> items = table.scan(scanSpec);

        System.out.println("\nSCANNING TABLE...");

        // Process each page of results
        int pageNum = 0;
        for (Page<Item, ScanOutcome> page : items.pages()) {
            System.out.println("\nPage: " + ++pageNum);

            // TODO 6 BEGIN
            //Process each item on the current page using page iterator
            Iterator<Item> item = page.iterator();
            // TODO 6 END
            
            //Process each item using an iterator
//        Iterator<Item> item = items.iterator();
            while (item.hasNext()) {
                System.out.println(item.next().toJSONPretty());
            }
        }
    }
}





