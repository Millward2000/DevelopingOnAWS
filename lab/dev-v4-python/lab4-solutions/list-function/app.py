# PollyNotes-ListFunction
#
# This lambda function is integrated with the following API methods:
# /notes GET (list operation)
#
# Its purpose is to get notes from our DynamoDB table

from __future__ import print_function
import boto3
import os
from boto3.dynamodb.conditions import Key

dynamoDBResource = boto3.resource('dynamodb')


def lambda_handler(event, context):
    # Log debug information
    print(event)
    ddbTable = os.environ['TABLE_NAME']

    # Get the database items from the pollynotes table
    databaseItems = getDatabaseItems(dynamoDBResource, ddbTable, event)

    return databaseItems


def getDatabaseItems(dynamoDBResource, ddbTable, event):
    print("getDatabaseItems Function")

    # Create our DynamoDB table resource
    table = dynamoDBResource.Table(ddbTable)

    # If a userId was passed, query the table for that user's items
    if "UserId" in event:
        UserId = event['UserId']
        records = table.query(KeyConditionExpression=Key("UserId").eq(UserId))
    else:
        # if not, scan the table and return all items
        records = table.scan()

    return records["Items"]
