# PollyNotes-DeleteFunction
# This function allows us to delete items in DynamoDB
#
# This lambda function is integrated with the following API method:
# /notes/{id} DELETE (delete a note)

from __future__ import print_function
import boto3
import os

dynamoDBResource = boto3.resource('dynamodb')


def lambda_handler(event, context):

    # Log debug information
    print(event)

    # Extract the user parameters from the event and environment
    UserId = event["UserId"]
    NoteId = event["NoteId"]
    ddbTable = os.environ['TABLE_NAME']

    deletedNoteId = deleteItem(dynamoDBResource, ddbTable, UserId, NoteId)

    return deletedNoteId


def deleteItem(dynamoDBResource, ddbTable, UserId, NoteId):
    print('deleteItem function')

    # set the table's name identifier
    table = dynamoDBResource.Table(ddbTable)

    # Delete the note
    table.delete_item(
        Key={
            'UserId': UserId,
            'NoteId': int(NoteId)
        }
    )

    return NoteId
