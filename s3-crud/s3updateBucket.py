# Modify a bucket to enable S3 versioning
import boto3

client = boto3.client('s3')

bucketName = 'millwam-crud-bucket'

client.put_bucket_versioning(
    Bucket = bucketName,
    VersioningConfiguration={
        'Status' : 'Enabled'
    }
)

Status = client.get_bucket_versioning(
    Bucket = bucketName
)

print('S3 bucket versioning is currently ' + Status.get('Status') + ' for the bucket ' + bucketName)
