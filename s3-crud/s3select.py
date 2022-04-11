import boto3
from botocore.config import Config

my_config = Config(
    region_name = 'af-south-1'
)

client = boto3.client("s3", config=my_config)

response = client.select_object_content(
    Bucket='millwam-crud-bucket',
    Key='mcu.csv',
    Expression="select s._2, s._3 from s3object s where s._2 = 'STEVE ROGERS'",
    ExpressionType='SQL',
    InputSerialization={
        'CSV': {
            'AllowQuotedRecordDelimiter': True,
            'FileHeaderInfo': 'None'
        },
        'CompressionType': 'NONE'

    },
    OutputSerialization={
        'CSV': {
        }
    }
    )

for event in response['Payload']:
    if 'Records' in event:
        records = event['Records']['Payload'].decode('utf-8')
        print(records)
    elif 'Stats' in event:
        statsDetails = event['Stats']['Details']
        print("Stats details bytesScanned: ")
        print(statsDetails['BytesScanned'])
        print("Stats details bytesProcessed: ")
        print(statsDetails['BytesProcessed'])