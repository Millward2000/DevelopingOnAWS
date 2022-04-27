import boto3

from boto3.dynamodb.conditions import Key

dynamodb = boto3.resource('dynamodb', endpoint_url='http://127.0.0.1:8000')
table = dynamodb.Table('music')

songName=input('Which Song do you want to query?')

response = table.query(
  KeyConditionExpression=Key('trackName').eq(songName)
)
print(response['Items'])