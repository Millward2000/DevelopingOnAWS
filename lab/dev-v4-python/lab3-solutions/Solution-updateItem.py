import boto3, botocore, configparser


def main(ddbClient):
    
    config = readConfig()

    tableName = config['tableName']

    qUserId = config['queryUserId']
    qNoteId = config['queryNoteId']
    notePrefix = config['notePrefix']
    

    print("\nUpdating the note flag for remediation...\n")
    print(updateNewAttribute(ddbClient, tableName, qUserId, qNoteId))

    print("\nRemediating the marked note...\n")
    print(updateExistingAttributeConditionally(ddbClient, tableName, qUserId, qNoteId, notePrefix))


def updateNewAttribute(ddbClient, tableName, qUserId, qNoteId):
    
    ## TODO 7: Add code to set an 'Is_Incomplete' flag to 'Yes' for the note that matches the 
    ## provided function parameters
    
    response = ddbClient.update_item(
        TableName=tableName,
        Key={
            'UserId': {'S': qUserId},
            'NoteId': {'N': str(qNoteId)}
        },
        ReturnValues='ALL_NEW',
        UpdateExpression='SET Is_Incomplete = :incomplete',
        ExpressionAttributeValues={
            ':incomplete': {'S': 'Yes'}
        }
    )

    ## End TODO 7
    return response['Attributes']


def updateExistingAttributeConditionally(ddbClient, tableName, qUserId, qNoteId, notePrefix):
    try:
        ## TODO 8: Add code to update the Notes attribute for the note that matches 
        # the passed function parameters only if the 'Is_Incomplete' attribute is 'Yes'
        
        notePrefix += ' 400 KB'
        ## End TODO 8
        response = ddbClient.update_item(
            TableName=tableName,
            Key={
                'UserId': {'S': qUserId},
                'NoteId': {'N': str(qNoteId)}
            },
            ReturnValues='ALL_NEW',
            UpdateExpression='SET Note = :NewNote, Is_Incomplete = :new_incomplete',
            ConditionExpression='Is_Incomplete = :old_incomplete',
            ExpressionAttributeValues={
                ':NewNote': {'S': notePrefix},
                ':new_incomplete': {'S': 'No'},
                ':old_incomplete': {'S': 'Yes'}
            }
        )
        
        return response['Attributes']
    except botocore.exceptions.ClientError as err:
        if err.response['Error']['Code'] == 'ConditionalCheckFailedException':
            return "Sorry, your update is invalid mate!"
        else:
            return err.response['Error']['Message']

    

## Utility methods
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
