import boto3

dynamodb = boto3.resource('dynamodb', endpoint_url='http://127.0.0.1:8000')
table = dynamodb.Table('music')

response = table.get_item(
    Key={
        'trackName': 'Shake the Disease',
        'endTime': '2021-01-04 14:31'
    }
)
print(response['Item'])

