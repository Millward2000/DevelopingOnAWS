import boto3, botocore, json, decimal, configparser
from boto3.dynamodb.conditions import Key, Attr
from boto3.dynamodb.types import TypeDeserializer


def main(ddbClient):
    
    ##load configuration data from file
    config = readConfig()

    tableName = config['tableName']
    pageSize = config['pageSize']

    print("\n************\nScanning with pagination...\n")
    printNotes(queryAllNotesPaginator(ddbClient, tableName, pageSize))

def queryAllNotesPaginator(ddbClient, tableName, pageSize):

    ## TODO 6: Add code that creates a paginator and uses the printNotes function 
    # to print the items returned in each page.
    
    # Create a reusable Paginator
    paginator = ddbClient.get_paginator('scan')

    # Create a PageIterator from the Paginator
    page_iterator = paginator.paginate(
        TableName=tableName,
        PaginationConfig={
            'PageSize': pageSize
        })

    pageNumber = 0
    for page in page_iterator:
        if page["Count"] > 0:
            pageNumber += 1
            print("Starting page " + str(pageNumber))
            printNotes(page['Items'])
            print("End of page " + str(pageNumber) + "\n")
    
    ## End TODO 6

## Utility methods
def printNotes(notes):
    if isinstance(notes, list):
        for note in notes:
            print(
                json.dumps(
                    {key: TypeDeserializer().deserialize(value) for key, value in note.items()},
                    cls=DecimalEncoder
                )
            )

# Helper class to convert a DynamoDB item to JSON.
class DecimalEncoder(json.JSONEncoder):
    def default(self, o):
        if isinstance(o, decimal.Decimal):
            return str(o)
        if isinstance(o, set):  # <---resolving sets as lists
            return list(o)
        return super(DecimalEncoder, self).default(o)

def readConfig():
    config = configparser.ConfigParser()
    config.read('config.ini')

    return config['DynamoDB']

client = boto3.client('dynamodb')

try:
    main(client)
except botocore.exceptions.ClientError as err:
    print(err.response['Error']['Message'])
except botocore.exceptions.ParamValidationError as error:
    print(error)
