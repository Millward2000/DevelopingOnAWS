# PollyNotes-SearchFunction
#
# This lambda function is integrated with the following API method:
# /notes/search GET (search)
#
# Its purpose is to get notes from our DynamoDB table

from __future__ import print_function
import boto3
import os
from boto3.dynamodb.conditions import Key, Attr

dynamoDBResource = boto3.resource('dynamodb')

def lambda_handler(event, context):

    # Log debug information
    print(event)

    # Extracting the user parameters from the event and environment
    UserId = event["UserId"]
    filterText = event["text"]
    ddbTable = os.environ['TABLE_NAME']

    # Get the database items from the pollynotes table filtered with the passed text
    databaseItems = getDatabaseItems(dynamoDBResource, ddbTable, UserId, filterText)

    return databaseItems

def getDatabaseItems(dynamoDBResource, ddbTable, UserId, filterText):
    print("getDatabaseItems Function")
  
    # Create our DynamoDB table resource
    table = dynamoDBResource.Table(ddbTable)

    # Query the table for all items with the UserId and query text
    records = table.query(
        KeyConditionExpression=Key("UserId").eq(UserId),
        FilterExpression=Attr("Note").contains(filterText)
    )
    
    return records["Items"]