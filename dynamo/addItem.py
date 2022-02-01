# Adds a single item to the music Table
import boto3

dynamodb = boto3.resource('dynamodb', endpoint_url='http://127.0.0.1:8000')
table = dynamodb.Table('music')

new_item = table.put_item(
   Item={
        'endTime': '2021-01-04 14:31',
        'artistName': 'Depeche Mode',
        'trackName': 'Shake the Disease',
        'msPlayed': 29959,
    }
)

print(table.item_count)