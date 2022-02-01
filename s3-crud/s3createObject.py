# Create a new object in the bucket
import boto3

bucketName = 'millwam-crud-bucket'
regionName = 'af-south-1'
key = 'testfile.txt'

s3 = boto3.resource('s3')
object = s3.Object(bucketName,key).upload_file('/home/matt/aai/dev/demo/s3-crud/testfile.txt')