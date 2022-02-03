import boto3, botocore, json, decimal, configparser
from boto3.dynamodb.conditions import Key, Attr
from boto3.dynamodb.types import TypeDeserializer

def main(ddbClient):
    
    ##load configuration data from file
    config = readConfig()

    tableName = config['tableName']

    UserId = config['queryUserId']
    NoteId = config['queryNoteId']
    
    print("\n************\nQuerying for note " + str(NoteId) + " that belongs to user " + UserId + "...\n")
    printNotes(querySpecificNote(ddbClient, tableName, UserId, NoteId))

def querySpecificNote(ddbClient, tableName, qUserId, qNoteId):
    ## TODO 9: Using PartiQL, add code to query for a specific note with the parameter 
    # values available for use.
    
    response = ddbClient.execute_statement(
        Statement="SELECT * FROM " + tableName + " WHERE UserId = ? AND NoteId = ?",
        Parameters=[
            {"S": qUserId},
            {"N": str(qNoteId)}
        ]
    )
    
    ## End TODO 9
    
    return response["Items"]
    
## Utility methods

# Helper function to deserialize DynamoDB JSON ('Notes': {'S': 'Note text'}) 
# to standard JSON ('Notes': 'Note text')
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