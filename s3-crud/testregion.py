import boto3

bucketName = 'millwam-crud-bucket'

client = boto3.client('s3')
response = client.get_bucket_location(
    Bucket=bucketName
)

print (response)