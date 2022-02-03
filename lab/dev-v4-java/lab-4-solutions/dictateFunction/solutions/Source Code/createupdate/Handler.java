package createupdate;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.HashMap;
import java.util.Map;

public class Handler implements RequestHandler<Map<String, String>, String> {

    private static final Logger logger = LoggerFactory.getLogger(Handler.class);
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final DynamoDbAsyncClient client = DynamoDbAsyncClient.builder()
            .overrideConfiguration(ClientOverrideConfiguration.builder()
                    .build())
            .build();

    @Override
    public String handleRequest(Map<String, String> event, Context context) {
        String response;
        try {
            logger.info("environment: {}", gson.toJson(System.getenv()));
            String tableName = System.getenv("TABLE_NAME");

            logger.info("event: {}", gson.toJson(event));
            String userId = event.get("UserId");
            String noteId = event.get("NoteId");
            String notes = event.get("Note");
            String voice = event.get("VoiceId");

            logger.info("Userid: {}", userId);
            logger.info("id: {}", noteId);
            logger.info("note: {}", notes);
            logger.info("voice: {}", voice);
            logger.info("table: {}", tableName);

            var attributes = new HashMap<String, AttributeValue>();

            attributes.put("UserId", AttributeValue.builder().s(userId).build());
            attributes.put("NoteId", AttributeValue.builder().n(noteId).build());
            attributes.put("Note", AttributeValue.builder().s(notes).build());

            var request = PutItemRequest.builder()
                    .tableName(tableName)
                    .item(attributes)
                    .build();

            logger.info("creating id: {}", noteId);

            client.putItem(request).join();
            response = noteId;

        } catch (Exception e) {
            throw e;
        } finally {
        }
        return response;
    }
}