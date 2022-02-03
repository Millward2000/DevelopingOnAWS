package list;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;

import java.util.HashMap;
import java.util.Map;


public class Handler implements RequestHandler<Map<String, String>, Object> {
    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Override
    public Object handleRequest(Map<String, String> event, Context context) {

        Object response;
        try {
            logger.info("environment: {}", gson.toJson(System.getenv()));
            logger.info("event: {}", gson.toJson(event));

            String noteId = event.get("NoteId");
            String voice = event.get("VoiceId");
            String userId = event.get("UserId");
            String bucket = System.getenv("MP3_BUCKET_NAME");
            String tableName = System.getenv("TABLE_NAME");

            logger.info("Userid: {}", userId);
            logger.info("id: {}", noteId);
            logger.info("voice: {}", voice);
            logger.info("bucket: {}", bucket);
            logger.info("table: {}", tableName);

            var client = DynamoDbAsyncClient.builder()
                    .overrideConfiguration(ClientOverrideConfiguration.builder()
                            .build())
                    .build();

            var attributes = new HashMap<String, AttributeValue>();
            attributes.put(":v1", AttributeValue.builder().s(userId).build());
            var request = QueryRequest.builder()
                    .tableName(tableName)
                    .keyConditionExpression("UserId = :v1")
                    .expressionAttributeValues(attributes)
                    .build();

            var queryResponse = client.query(request);
            var notes = queryResponse.join().items().stream().map(n -> toNote(n)).toArray();

            response = notes;
            logger.info("Notes to be returned: {}", gson.toJson(notes));
        } catch (Exception e) {
            throw e;
        } finally {
            logger.info("processed the call");
        }
        return response;
    }

    private Note toNote(Map<String, AttributeValue> item) {
        var rc = new Note();
        var attrsSet = 0;
        for (var key : item.keySet()) {
            attrsSet++;
            if (key == "Note") {
                rc.Note = item.get(key).s();
            } else if (key == "NoteId") {
                rc.NoteId = item.get(key).n();
            } else if (key == "UserId") {
                rc.UserId = item.get(key).s();
            } else {
                logger.warn("unknown DDB attribute {}", key);
                attrsSet--;
            }
        }
        return rc;
    }

}
