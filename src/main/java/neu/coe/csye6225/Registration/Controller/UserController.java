package neu.coe.csye6225.Registration.Controller;

import neu.coe.csye6225.Registration.Entity.User;
import neu.coe.csye6225.Registration.Exception.EmailNotValidException;
import neu.coe.csye6225.Registration.Service.UserService;
import neu.coe.csye6225.Registration.Util.StatsdClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private StatsdClient statsdClient;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping(path="")
    public ResponseEntity create(@RequestBody User user) {
        statsdClient.increment("user.post");
        logger.info("Creating user...");
        User create_user = userService.create(user);
        return new ResponseEntity(create_user, HttpStatus.OK);
    }

    @PutMapping(path="/self")
    public ResponseEntity<String> update(@RequestBody User uu, @RequestHeader("authorization") String authorization){
        statsdClient.increment("user.put");
        logger.info("Updating user...");
        userService.update(uu,authorization);
        return new ResponseEntity<String>("Updated", HttpStatus.NO_CONTENT);
    }

    @GetMapping(path="/self")
    public ResponseEntity<User> get(@RequestHeader("authorization") String authorization){
        statsdClient.increment("user.get");
        logger.info("Getting user...");
        User u = userService.get(authorization);
        return new ResponseEntity<User>(u,HttpStatus.OK);
    }
}
