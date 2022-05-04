package neu.coe.csye6225.Registration.Controller;

import neu.coe.csye6225.Registration.Entity.User;
import neu.coe.csye6225.Registration.Exception.EmailLinkExpiredException;
import neu.coe.csye6225.Registration.Exception.EmailNotVerifiedException;
import neu.coe.csye6225.Registration.Exception.EmailTokenNotCorrectException;
import neu.coe.csye6225.Registration.Repository.UserRepository;
import neu.coe.csye6225.Registration.Util.DynamoDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;


@RestController
public class SESController {
    @Autowired
    private DynamoDbClient ddb;

    @Value("${topicArn}")
    private String topicArn;

    @Value("${dynamoTableName}")
    private String tableName;

    @Autowired // to get the bean which is auto-generated by Spring
    private UserRepository userRepository;

    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @GetMapping(path="/v1/verifyUserEmail")
    public ResponseEntity<String> verifyEmail(@RequestParam("email")String email, @RequestParam("token")String token){
        logger.info(String.format("Verifying user %s with token %s...",email,token));
        long exp = DynamoDB.queryExpirationTime(ddb,tableName,"Email",email,"#a");
        long cur = System.currentTimeMillis()/1000;
        if(exp==-1 || exp < cur){
            return new ResponseEntity<>("Email link has expired.", HttpStatus.BAD_REQUEST);
        }
        String t_token = DynamoDB.queryToken(ddb,tableName,"Email",email,"#a");
        if(t_token.equals(token)){
            User u = userRepository.findById(email).get();
            u.setVerified(true);
            userRepository.save(u);
        }
        else{
            return new ResponseEntity<>("Email token is not correct.", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Email successfully verified!", HttpStatus.OK);
    }
}