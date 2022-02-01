# Add Spotify listening history to DynamoDB Local using a batch update (adapted from https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/GettingStarted.Python.02.html )

from decimal import Decimal
import json
import boto3


def load_music(music, dynamodb=None):
    if not dynamodb:
        dynamodb = boto3.resource('dynamodb', endpoint_url="http://127.0.0.1:8000")

    table = dynamodb.Table('music')
    for song in music:
        endTime = song['endTime']
        trackName = song['trackName']
        print("Adding Track:", endTime, trackName)
        table.put_item(Item=song)


if __name__ == '__main__':
    with open("/home/matt/aai/dev/demo/dynamo/MyData/StreamingHistory0.json") as json_file:
        music_list = json.load(json_file, parse_float=Decimal)
    load_music(music_list)
