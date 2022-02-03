# PollyNotes-CreateUpdateFunction
# This function allows us to create and update items in DynamoDB
#
# This lambda function is integrated with the following API method:
# /notes POST (create or update a note)

from __future__ import print_function
import boto3
import os

dynamoDBResource = boto3.resource('dynamodb')

def lambda_handler(event, context):
    
    # Log debug information
    print(event)
    
    # Extracting the user parameters from the event
    UserId = event["UserId"]
    NoteId = event['NoteId']
    Note = event['Note']
    ddbTable = os.environ['TABLE_NAME']
    
    # DynamoDB 'put_item' to add or update a note
    newNoteId = upsertItem(dynamoDBResource, ddbTable, UserId, NoteId, Note)

    return newNoteId

def upsertItem(dynamoDBResource, ddbTable, UserId, NoteId, Note):
    print('upsertItem Function')

    # set the table's name identifier
    table = dynamoDBResource.Table(ddbTable)
    
    # Put the item in the database, this will create a new item if the UserId and NoteId
    # do not match an existing note. If it does, it will update that note.
    table.put_item(
        Item={
            'UserId': UserId,
            'NoteId': int(NoteId),
            'Note': Note
        }
    )
    return NoteId