package neu.coe.csye6225.Registration.Util;

// snippet-start:[dynamodb.java2.put_item.import]
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;
import software.amazon.awssdk.services.dynamodb.model.ResourceNotFoundException;
import software.amazon.awssdk.services.dynamodb.model.QueryRequest;
import software.amazon.awssdk.services.dynamodb.model.QueryResponse;
import java.util.HashMap;
// snippet-end:[dynamodb.java2.put_item.import]


/**
 * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
 *
 * For information, see this documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 *
 *  To place items into an Amazon DynamoDB table using the AWS SDK for Java V2,
 *  its better practice to use the
 *  Enhanced Client. See the EnhancedPutItem example.
 */
@Configuration
public class DynamoDB {

    @Bean
    public DynamoDbClient DynamoDBClient(){
        Region region = Region.US_EAST_1;
        DynamoDbClient ddb = DynamoDbClient.builder()
                .region(region)
                .build();
        return ddb;
    }
    // snippet-start:[dynamodb.java2.put_item.main]
    public static void putItemInTable(DynamoDbClient ddb,
                                      String tableName,
                                      String keyVal,
                                      String tokenValue,
                                      Boolean sentVal,
                                      String expirationTimeVal){
        HashMap<String,AttributeValue> itemValues = new HashMap<String,AttributeValue>();
        // Add all content to the table
        itemValues.put("Email", AttributeValue.builder().s(keyVal).build());
        itemValues.put("Token", AttributeValue.builder().s(tokenValue).build());
        itemValues.put("Sent", AttributeValue.builder().bool(sentVal).build());
        itemValues.put("ExpirationTime", AttributeValue.builder().n(expirationTimeVal).build());
        PutItemRequest request = PutItemRequest.builder()
                .tableName(tableName)
                .item(itemValues)
                .build();

        try {
            ddb.putItem(request);
            System.out.println(tableName +" was successfully updated");

        } catch (ResourceNotFoundException e) {
            System.err.format("Error: The Amazon DynamoDB table \"%s\" can't be found.\n", tableName);
            System.err.println("Be sure that it exists and that you've typed its name correctly!");
            System.exit(1);
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }
    // snippet-end:[dynamodb.java2.put_item.main]

    // snippet-start:[dynamodb.java2.query.main]
    public static long queryExpirationTime(DynamoDbClient ddb,
                                 String tableName,
                                 String partitionKeyName,
                                 String partitionKeyVal,
                                 String partitionAlias) {


        // Set up an alias for the partition key name in case it's a reserved word
        HashMap<String,String> attrNameAlias = new HashMap<String,String>();
        attrNameAlias.put(partitionAlias, partitionKeyName);

        // Set up mapping of the partition name with the value
        HashMap<String, AttributeValue> attrValues =
                new HashMap<String,AttributeValue>();

        attrValues.put(":"+partitionKeyName, AttributeValue.builder()
                .s(partitionKeyVal)
                .build());

        QueryRequest queryReq = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression(partitionAlias + " = :" + partitionKeyName)
                .expressionAttributeNames(attrNameAlias)
                .expressionAttributeValues(attrValues)
                .build();
        try {
            QueryResponse response = ddb.query(queryReq);
            return Long.parseLong(response.items().get(0).get("ExpirationTime").n());
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (NullPointerException | IndexOutOfBoundsException e){
            System.err.println(e.getMessage());
            return -1;
        }
        return -1;
    }
    // snippet-end:[dynamodb.java2.query.main]

    public static String queryToken(DynamoDbClient ddb,
                                           String tableName,
                                           String partitionKeyName,
                                           String partitionKeyVal,
                                           String partitionAlias) {


        // Set up an alias for the partition key name in case it's a reserved word
        HashMap<String,String> attrNameAlias = new HashMap<String,String>();
        attrNameAlias.put(partitionAlias, partitionKeyName);

        // Set up mapping of the partition name with the value
        HashMap<String, AttributeValue> attrValues =
                new HashMap<String,AttributeValue>();

        attrValues.put(":"+partitionKeyName, AttributeValue.builder()
                .s(partitionKeyVal)
                .build());

        QueryRequest queryReq = QueryRequest.builder()
                .tableName(tableName)
                .keyConditionExpression(partitionAlias + " = :" + partitionKeyName)
                .expressionAttributeNames(attrNameAlias)
                .expressionAttributeValues(attrValues)
                .build();
        try {
            QueryResponse response = ddb.query(queryReq);
            return response.items().get(0).get("Token").s();
        } catch (DynamoDbException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        } catch (NullPointerException e){
            System.err.println(e.getMessage());
            return "null";
        }
        return "null";
    }
}