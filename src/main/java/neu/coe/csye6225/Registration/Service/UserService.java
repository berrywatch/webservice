package neu.coe.csye6225.Registration.Service;

import neu.coe.csye6225.Registration.Entity.User;
import neu.coe.csye6225.Registration.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Service
public class UserService {
    @Autowired // to get the bean which is auto-generated by Spring
    private UserRepository userRepository;

    private BCryptPasswordEncoder encoder;

    public UserService() {
        encoder = new BCryptPasswordEncoder();
    }

    public User create(User user) {
        if (userRepository.findById(user.getUsername()).isPresent()) {
            return null;
        }
        String timeStamp = new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date());
        user.setAccount_created(timeStamp);
        user.setAccount_updated(timeStamp);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        user.setUuid(String.valueOf(UUID.randomUUID()));
        userRepository.save(user);
        return userRepository.findById(user.getUsername()).get();
    }

    public boolean update(User uu, String token) {
        String[] values = getAuthentication(token);
        if (values == null) return false;
        String username = values[0];
        String password = values[1];
        if(!checkAuthentication(username,password)){
            return false;
        }
        // check for fields
        if (uu.getAccount_created() != null || uu.getAccount_updated() != null || uu.getUsername() != null || uu.getUuid()!=null) {
            return false;
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
        return true;
    }

    public User get(String token) {
        String[] values = getAuthentication(token);
        if(values==null) return null;
        String username = values[0];
        String password = values[1];
        if(!checkAuthentication(username,password)){
            return null;
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

    private boolean checkAuthentication(String username, String password){
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
}
