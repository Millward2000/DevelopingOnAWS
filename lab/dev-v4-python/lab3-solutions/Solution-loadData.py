import boto3, botocore, configparser, json


def main(ddbResource):
    
    config = readConfig()
    tableName = config['tableName']
    jsonFileName = config['sourcenotes']

    # Opening JSON file
    f = open(jsonFileName)

    print("\n Loading \"" + tableName +
        "\" table with data from file \"" + jsonFileName + "\"\n\n")
    # Load json object from file
    notes = json.load(f)

    # Create dynamodb table resource
    table = ddbResource.Table(tableName)

    # Iterating through the notes and putting them in the table
    for n in notes:
        putNote(table, n)

    # Closing the JSON file
    f.close()
    print("\nFinished loading notes from the JSON file.\n")


def putNote(table, note):
    print("loading note " + str(note))
    ## TODO 4: Add code that uses the function parameters to 
    # add a new note to the table.
    
    table.put_item(
        Item={
            'UserId': note["UserId"],
            'NoteId': int(note["NoteId"]),
            'Note': note["Note"]
        }
    )
    
    ## END TODO 4
    

## Utility methods
def readConfig():
    config = configparser.ConfigParser()
    config.read('config.ini')

    return config['DynamoDB']

resource = boto3.resource('dynamodb')

try:
    main(resource)
except botocore.exceptions.ParamValidationError as error:
    print(error)