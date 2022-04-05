package neu.coe.csye6225.Registration.Controller;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3EncryptionClientV2Builder;
import com.amazonaws.services.s3.AmazonS3EncryptionV2;
import com.amazonaws.services.s3.model.*;
import neu.coe.csye6225.Registration.Entity.Picture;
import neu.coe.csye6225.Registration.Entity.User;
import neu.coe.csye6225.Registration.Exception.PictureFormatNotSupportException;
import neu.coe.csye6225.Registration.Exception.PictureNotFoundException;
import neu.coe.csye6225.Registration.Exception.UnauthorizedException;
import neu.coe.csye6225.Registration.Repository.UserRepository;
import neu.coe.csye6225.Registration.Service.PictureService;
import neu.coe.csye6225.Registration.Util.StatsdClient;
import org.apache.commons.io.FilenameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping(path="/v1/user/self/pic")
public class PictureController {

    @Autowired // to get the bean which is auto-generated by Spring
    private UserRepository userRepository;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private StatsdClient statsdClient;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @PostMapping(path="")
    public ResponseEntity<Picture> addProfile(@RequestParam("picture")MultipartFile file, @RequestHeader("authorization") String authorization){
        statsdClient.increment("user.pic.post");
        User u = getUser(authorization);
        if(u==null) {
            logger.error(String.format("User %s authorization failed", u.getUsername()));
            throw new UnauthorizedException();
        }
        // check for picture format
        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if(! (extension.equals("jpg") || extension.equals("png") || extension.equals("jpeg"))){
            logger.error("Picture format not be supported.");
            throw new PictureFormatNotSupportException();
        }
        logger.info("Saving picture...");
        Picture pic = pictureService.postPicture(file, u);
        return new ResponseEntity<Picture>(pic, HttpStatus.CREATED);
    }

    @GetMapping(path="")
    public ResponseEntity<Picture> getProfile(@RequestHeader("authorization") String authorization){
        statsdClient.increment("user.pic.get");
        User u = getUser(authorization);
        if(u==null) {
            logger.error(String.format("User %s authorization failed", u.getUsername()));
            throw new UnauthorizedException();
        }
        logger.info("Getting picture...");
        Picture pic = pictureService.getPicture(u);
        return new ResponseEntity<Picture>(pic, HttpStatus.OK);
    }

    @DeleteMapping(path="")
    public ResponseEntity<String> delProfile(@RequestHeader("authorization") String authorization){
        statsdClient.increment("user.pic.delete");
        User u = getUser(authorization);
        if(u==null) {
            logger.error(String.format("User %s authorization failed", u.getUsername()));
            throw new UnauthorizedException();
        }
        Picture pic = pictureService.getPicture(u);
        logger.info("Deleting picture...");
        pictureService.deletePicture(u);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    // Get user information from token. If unauthorized, return null.
    public User getUser(String token) {
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
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(password, old_pwd)) {
            return false;
        }
        return true;
    }
}
