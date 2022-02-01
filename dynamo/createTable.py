# Creates a table called music using dynamodb local (just remove the endpoint_url to port this to production/cloud)

import boto3

dynamodb = boto3.resource('dynamodb', endpoint_url='http://127.0.0.1:8000')
table = dynamodb.create_table(
    TableName='music',
    AttributeDefinitions=[
        {
            'AttributeName': 'trackName',
            'AttributeType': 'S'
        },
        {
            'AttributeName': 'endTime',
            'AttributeType': 'S'
        }
    ],
    KeySchema=[
        {
            'AttributeName': 'trackName',
            'KeyType': 'HASH'
        },
        {
            'AttributeName': 'endTime',
            'KeyType': 'RANGE'
        }
    ],
    ProvisionedThroughput={
        'ReadCapacityUnits': 5,
        'WriteCapacityUnits': 5
    }
)

table.wait_until_exists()

print('Congrats, your table is now ' + table.table_status)
