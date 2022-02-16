package neu.coe.csye6225.Registration.Controller;

import neu.coe.csye6225.Registration.Entity.User;
import neu.coe.csye6225.Registration.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path="/v1/user")
public class MainController {


    @Autowired
    private UserService userService;

    @PostMapping(path="")
    public ResponseEntity create(@RequestBody User user){
        // check for existence
        User create_user = userService.create(user);
        if(create_user==null)
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        return new ResponseEntity(create_user, HttpStatus.OK);
    }

    @PutMapping(path="/self")
    public ResponseEntity<String> update(@RequestBody User uu, @RequestHeader("authorization") String authorization){
        if(!userService.update(uu,authorization)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Updated", HttpStatus.NO_CONTENT);
    }

    @GetMapping(path="/self")
    public ResponseEntity<User> get(@RequestHeader("authorization") String authorization){
        User u = userService.get(authorization);
        if(u==null) return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        return new ResponseEntity<User>(u,HttpStatus.OK);
    }
}
