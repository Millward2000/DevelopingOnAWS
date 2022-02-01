# Generates a presigned URL to allow temporary access to the file

import boto3
from botocore.exceptions import ClientError

bucketName = 'millwam-crud-bucket'
key = 'testfile.txt'

def generate_presigned_url(bucket_name, object_key, expiry):

    client = boto3.client('s3', endpoint_url='https://s3.af-south-1.amazonaws.com')

    try:
        response = client.generate_presigned_url('get_object',
                                                  Params={'Bucket': bucket_name,'Key': object_key},
                                                  ExpiresIn=expiry)
        print(response)

    except ClientError as e:
        print(e)

generate_presigned_url(bucketName,key,600)