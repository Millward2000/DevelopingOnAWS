# Create a large object in the bucket and use a callback to track the upload progress
import os
import boto3
import progressbar

def upload_to_aws(local_file, bucket, key):
    s3 = boto3.client('s3')

    statinfo = os.stat(local_file)
    up_progress = progressbar.progressbar.ProgressBar(maxval=statinfo.st_size)
    up_progress.start()
    def upload_progress(chunk):
        up_progress.update(up_progress.currval + chunk)

    try:
        s3.upload_file(local_file, bucket, key, Callback=upload_progress)
        up_progress.finish()
        print("Upload Successful")
        return True
    except FileNotFoundError:
        print("The file was not found")
        return False

upload_to_aws('/home/matt/aai/dev/demo/s3-crud/mcu.csv', 'millwam-crud-bucket','mcu.csv')