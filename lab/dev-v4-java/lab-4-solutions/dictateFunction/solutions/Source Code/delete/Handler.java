package delete;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.DeleteItemRequest;

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

            logger.info("deleting id: {}", noteId);

            var key = new HashMap<String, AttributeValue>();
            key.put("UserId", AttributeValue.builder().s(userId).build());
            key.put("NoteId", AttributeValue.builder().n(noteId).build());

            var request = DeleteItemRequest.builder()
                    .tableName(tableName)
                    .key(key)
                    .build();

            client.deleteItem(request).join();
            response = noteId;

        } catch (Exception e) {
            throw e;
        } finally {
            logger.warn("Lambda call ended");
        }
        return response;
    }
}
