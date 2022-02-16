package neu.coe.csye6225.Registration.Controller;

import neu.coe.csye6225.Registration.Entity.User;
import neu.coe.csye6225.Registration.Entity.UserUpdate;
import neu.coe.csye6225.Registration.Repository.UserRepository;
import neu.coe.csye6225.Registration.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;

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
    public ResponseEntity<String> update(@RequestBody UserUpdate uu){

        if(!userService.update(uu)){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Updated", HttpStatus.NO_CONTENT);
    }

//    @GetMapping(path="/self")
//    public ResponseEntity<User> get(@RequestParam(defaultValue = "") String email, @RequestHeader("Authorization") String token){
//        if(!token.equals(this.token)){
//            return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
//        }
//        if(!userRepository.findById(email).isPresent()){
//            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
//        }
//        Optional<User> u = userRepository.findById(email);
//        return new ResponseEntity<User>(u.get(),HttpStatus.OK);
//    }
}
