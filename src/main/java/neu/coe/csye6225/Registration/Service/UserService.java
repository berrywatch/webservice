package neu.coe.csye6225.Registration.Service;

import neu.coe.csye6225.Registration.Entity.User;
import neu.coe.csye6225.Registration.Exception.*;
import neu.coe.csye6225.Registration.Repository.UserRepository;
import neu.coe.csye6225.Registration.Util.DynamoDB;
import neu.coe.csye6225.Registration.Util.SNSTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.sns.SnsClient;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class UserService {
    @Autowired // to get the bean which is auto-generated by Spring
    private UserRepository userRepository;

    @Autowired
    private DynamoDbClient ddb;

    @Autowired
    private SnsClient snsClient;

    @Value("${topicArn}")
    private String topicArn;

    @Value("${dynamoTableName}")
    private String tableName;

    @Value("${domain}")
    private String domain;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private BCryptPasswordEncoder encoder;

    public UserService() {
        encoder = new BCryptPasswordEncoder();
    }

    // Create and save a model user from request user.
    public User create(User user) {
        // if username is null or doesn't exist, throw exception
        logger.debug("Checking http request body format...");
        if (user.getUsername() == null || user.getUsername().equals("")) {
            throw new EmailNotValidException();
        }
        // if username is not a valid email, throw exception
        if (!validateEmail(user.getUsername())) {
            throw new EmailNotValidException();
        }
        // if username already exists, return null
        if (userRepository.findById(user.getUsername()).isPresent() && userRepository.findById(user.getUsername()).get().getVerified()) {
            throw new EmailExistsException();
        }
        logger.debug("User format correct. Check if user has been in dynamodb...");
        // check record in DynamoDB, publish topic to SNS
        long exp = DynamoDB.queryExpirationTime(ddb, tableName, "Email", user.getUsername(), "#a");
        long cur = System.currentTimeMillis() / 1000;
        if (exp == -1 || exp < cur) {
            String expTime = String.valueOf(cur + 120);
            String token = String.valueOf(UUID.randomUUID());
            logger.info(String.format("Writing user %s with expiration time %s info in dynamodb...", user.getUsername(), expTime));
            DynamoDB.putItemInTable(ddb, tableName, user.getUsername(), token, false, expTime);
            logger.info("Publishing topic to SNS...");
            SNSTopic.publishTopic(snsClient, topicArn, "hello", user.getUsername(), token, domain+"/v1/verifyUserEmail", expTime);
        } else {
            throw new EmailLinkNotExpiredException();
        }

        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        user.setAccount_created(timeStamp);
        user.setAccount_updated(timeStamp);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setUuid(String.valueOf(UUID.randomUUID()));
        user.setVerified(false);
        logger.debug("User has been created successfully. Saving user to database...");
        userRepository.save(user);
        return userRepository.findById(user.getUsername()).get();
    }

    // Update user information. Username, uuid, account_created, account_updated  fields are not allowed, return false if they exist.
    public void update(User uu, String token) {
        String[] values = getAuthentication(token);
        if (values == null) throw new UnauthorizedException();
        String username = values[0];
        String password = values[1];
        if (!checkAuthentication(username, password)) {
            throw new UnauthorizedException();
        }
        if(!verifyEmail(username)){
            throw new EmailNotVerifiedException();
        }
        // check for fields
        if (uu.getAccount_created() != null || uu.getAccount_updated() != null || uu.getUsername() != null || uu.getUuid() != null) {
            throw new BadFormatException();
        }
        // update values. account_created is not updatable due to Entity settings
        User u = userRepository.findById(username).get();
        if (uu.getFirst_name() != null)
            u.setFirst_name(uu.getFirst_name());
        if (uu.getLast_name() != null)
            u.setLast_name(uu.getLast_name());
        if (uu.getPassword() != null)
            u.setPassword(encoder.encode(uu.getPassword()));
        u.setAccount_updated(new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()));
        userRepository.save(u);
    }

    // Get user information from token. If unauthorized, return null.
    public User getUser(String token) {
        String[] values = getAuthentication(token);
        if (values == null) throw new UnauthorizedException();
        String username = values[0];
        String password = values[1];
        if (!checkAuthentication(username, password)) {
            logger.info(String.format("Email %s is unauthorized.",username));
            throw new UnauthorizedException();
        }
        if(!verifyEmail(username)){
            logger.info(String.format("Email %s not verified.",username));
            throw new EmailNotVerifiedException();
        }
        return userRepository.findById(username).get();
    }

    private String[] getAuthentication(String token) {
        if (token != null && token.toLowerCase().startsWith("basic")) {
            // Authorization: Basic base64credentials
            String base64Credentials = token.substring("Basic".length()).trim();
            byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
            String credentials = new String(credDecoded, StandardCharsets.UTF_8);
            // credentials = username:password
            final String[] values = credentials.split(":", 2);
            return values;
        }
        return null;
    }

    private boolean checkAuthentication(String username, String password) {
        // check username
        if (!userRepository.findById(username).isPresent()) {
            return false;
        }
        // check password
        String old_pwd = userRepository.findById(username).get().getPassword();
        if (!encoder.matches(password, old_pwd)) {
            return false;
        }
        return true;
    }

    private boolean verifyEmail(String email) {
        try{
            return userRepository.findById(email).get().getVerified();
        }
        catch (Exception e){
            System.err.println(e.getMessage());
        }
        return false;
    }

    private boolean validateEmail(String email) {
        return email.indexOf('@') != -1 && email.indexOf('.') != -1;
    }
}
