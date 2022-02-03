# This lambda function is will get a note text from DynamoDB,
# convert the text to speech and save it as an MP3 file,
# Save that MP3 file in S3 and return a generated signed URL to access that file.

from __future__ import print_function
import boto3
import os
from contextlib import closing

dynamoDBResource = boto3.resource('dynamodb')
pollyClient = boto3.client('polly')
s3Client = boto3.client('s3')


def lambda_handler(event, context):

    # Log debug information
    print(event)

    # Extract the user parameters from the event and environment
    UserId = event["UserId"]
    NoteId = event["NoteId"]
    VoiceId = event['VoiceId']
    mp3Bucket = os.environ['MP3_BUCKET_NAME']
    ddbTable = os.environ['TABLE_NAME']

    # Get the note text from the database
    text = getNote(dynamoDBResource, ddbTable, UserId, NoteId)

    # Save a MP3 file locally with the output from polly
    filePath = createMP3File(pollyClient, text, VoiceId, NoteId)

    # Host the file on S3 that is accessed by a pre-signed url
    signedURL = hostFileOnS3(s3Client, filePath, mp3Bucket, UserId, NoteId)

    return signedURL


def getNote(dynamoDBResource, ddbTable, UserId, NoteId):
    print("getNote Function")

    table = dynamoDBResource.Table(ddbTable)
    records = table.get_item(
        Key={
            'UserId': UserId,
            'NoteId': int(NoteId)
        }
    )
	# TODO 1: Get the note text from the pollynotes DynamoDB table that matches the UserId and NoteId
    return records['Item']['Note']
    # End TODO 1


def createMP3File(pollyClient, text, VoiceId, NoteId):
    print("createMP3File Function")
    # TODO 2: Use polly to convert the note text to speech using the VoiceId
    # and save the file as an MP3 in the /tmp folder
    pollyResponse = pollyClient.synthesize_speech(
        OutputFormat='mp3',
        Text=text,
        VoiceId=VoiceId
    )
	 # End TODO 2
    if "AudioStream" in pollyResponse:
        postId = str(NoteId)
        with closing(pollyResponse["AudioStream"]) as stream:
            filePath = os.path.join("/tmp/", postId)
            with open(filePath, "wb") as file:
                file.write(stream.read())

    return filePath


def hostFileOnS3(s3Client, filePath, mp3Bucket, UserId, NoteId):
    print("hostFileOnS3 Function")
    # TODO 3: Upload the mp3 file to S3 mp3Bucket and generate a pre-signed URL to access the MP3 object
    s3Client.upload_file(filePath,
                        mp3Bucket,
                        UserId+'/'+NoteId+'.mp3')
    # End TODO 3

    # Remove the file from the temp location to avoid potential data leaks
    os.remove(filePath)

    # Generate a pre-signed URL to access the MP3 object
    url = s3Client.generate_presigned_url(
        ClientMethod='get_object',
        Params={
            'Bucket': mp3Bucket,
            'Key': UserId+'/'+NoteId+'.mp3'
        }
    )

    return url
