# Check if a bucket exists using head bucket
import boto3,botocore

client = boto3.client('s3')

bucket = input("Bucket Name to be checked: ")

def verifyBucketName(s3Client, bucket):
    try:
        ## check if a bucket already exists in AWS
        s3Client.head_bucket(Bucket=bucket)
        # If the previous command is successful, the bucket is already in your account.
        raise SystemExit('This bucket has already been created and is owned by your account - please try again :(')
    except botocore.exceptions.ClientError as e:
        error_code = int(e.response['Error']['Code'])
        if error_code == 404:
          ## If you receive a 404 error code, a bucket with that name
          ##  does not exist anywhere in AWS.
          print('Existing Bucket Not Found, time to deploy that bucket!')
        if error_code == 403:
          ## If you receive a 403 error code, a bucket with that name exists 
          ## in another AWS account.
          raise SystemExit('This bucket has already owned by another AWS Account')

verifyBucketName(client,bucket)