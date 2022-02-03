import boto3, botocore, configparser


def main(ddbClient):
    
    config = readConfig()
    tableDefinition = {
        'tableName': config["tableName"],
        'primaryKey': config["primaryKey"],
        'sortKey': config["sortKey"],
        'readCapacity': config["readCapacity"],
        'writeCapacity': config["writeCapacity"]
        }
        
    print('\nCreating an Amazon DynamoDB table \"' + config["tableName"] + '\" \nwith a primary key: \"' +
        config["primaryKey"] + '\" \nand sort key: \"' + config["sortKey"] + '\".\n\n')
    creationResponse = createTable(ddbClient, tableDefinition)
    print('Table Status: ' + creationResponse['TableDescription']['TableStatus'])

    print('\nWaiting for the table to be available...\n')
    waitForTableCreation(ddbClient, config["tableName"])

    print('\nTable is now available.\n\n')
    tableOutput = getTableInfo(ddbClient, config["tableName"])
    print('Table Status: ' + tableOutput['Table']['TableStatus'])

def createTable(ddbClient, tableDefinition):
    ## TODO 2: Add logic to create a table with UserId as the 
    ## partition key and NoteId as the sort key

    response = ddbClient.create_table(
        AttributeDefinitions=[
            {
                'AttributeName': tableDefinition["primaryKey"],
                'AttributeType': 'S',
            },
            {
                'AttributeName': tableDefinition["sortKey"],
                'AttributeType': 'N',
            },
        ],
        KeySchema=[
            {
                'AttributeName': tableDefinition["primaryKey"],
                'KeyType': 'HASH',
            },
            {
                'AttributeName': tableDefinition["sortKey"],
                'KeyType': 'RANGE',
            },
        ],
        ProvisionedThroughput={
            'ReadCapacityUnits': int(tableDefinition["readCapacity"]),
            'WriteCapacityUnits': int(tableDefinition["writeCapacity"]),
        },
        TableName=tableDefinition["tableName"]
    )

    ## End TODO 2

    return response


def waitForTableCreation(ddbClient, tableName):
    ## TODO 3: Add a waiter to pause the script until your new table exists

    waiter = ddbClient.get_waiter('table_exists')

    waiter.wait(
        TableName=tableName
    )

    ## End TODO 3


def getTableInfo(ddbClient, tableName):
    response = ddbClient.describe_table(
        TableName=tableName
    )

    return response

## Utility methods
def readConfig():
    config = configparser.ConfigParser()
    config.read('config.ini')

    return config['DynamoDB']


## TODO 1: create an Amazon DynamoDB service client to pass to the
## main function.

client = boto3.client('dynamodb')

## End TODO 1

try:
    main(client)
except botocore.exceptions.ClientError as err:
    print(err.response['Error']['Message'])
except botocore.exceptions.ParamValidationError as error:
    print(error)
