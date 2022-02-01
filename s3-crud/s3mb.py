# Create a new bucket
import boto3

bucketName = 'millwam-crud-bucket'
regionName = 'af-south-1'

s3 = boto3.resource('s3')
bucket = s3.Bucket(bucketName)

bucket.create(
    CreateBucketConfiguration={
        'LocationConstraint' : regionName
    }
)
