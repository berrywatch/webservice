package neu.coe.csye6225.Registration.Util;

//snippet-start:[sns.java2.PublishTopic.import]
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.*;

import java.util.HashMap;
import java.util.Map;
//snippet-end:[sns.java2.PublishTopic.import]

@RestController
@Configuration
public class SNSTopic {
    /**
     * To run this Java V2 code example, ensure that you have setup your development environment, including your credentials.
     *
     * For information, see this documentation topic:
     *
     * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
     */
    @Bean
    public SnsClient snsClient(){
        SnsClient snsClient = SnsClient.builder()
                .region(Region.US_EAST_1)
                .build();
        return snsClient;
    }

    //snippet-start:[sns.java2.PublishTopic.main]
    public static void publishTopic(SnsClient snsClient, String topicArn, String message, String email, String token, String endpoint, String expirationTime) {
        Map<String, MessageAttributeValue> map = new HashMap<>();

        map.put("Email",MessageAttributeValue.builder().dataType("String").stringValue(email).build());
        map.put("Token",MessageAttributeValue.builder().dataType("String").stringValue(token).build());
        map.put("Endpoint",MessageAttributeValue.builder().dataType("String").stringValue(endpoint).build());
        map.put("ExpirationTime",MessageAttributeValue.builder().dataType("Number").stringValue(expirationTime).build());
        try {
            PublishRequest request = PublishRequest.builder()
                    .message(message)
                    .messageAttributes(map)
                    .topicArn(topicArn)
                    .build();
            PublishResponse result = snsClient.publish(request);
            System.out.println(result.messageId() + " Message sent. Status is " + result.sdkHttpResponse().statusCode());
        } catch (SnsException e) {
            System.err.println(e.awsErrorDetails().errorMessage());
            System.exit(1);
        }
    }
    //snippet-end:[sns.java2.PublishTopic.main]
}