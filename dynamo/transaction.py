import boto3

client = boto3.client('dynamodb', endpoint_url='http://127.0.0.1:8000')

response = client.transact_write_items(
    TransactItems=[
        {
            'Put': {
                'TableName': 'music',
                'Item': {
                    'trackName': { 'S': 'I never heard it before' },
                    'endTime': { 'S': '2022-06-01 17:16' },
                    'artistName': { 'S': 'Depeche Mode' }
                }
            }
        },
        {
            'Put': {
                'TableName': 'music',
                'Item': {
                    'trackName': { 'S': 'I never heard it before' },
                    'endTime': { 'S': '2022-06-02 18:10' },
                    'artistName': { 'S': 'Depeche Mode' }
                },
                'ConditionExpression': 'attribute_exists(msPlayed)'
            }
        },
    ]
)


