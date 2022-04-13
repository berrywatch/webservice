package neu.coe.csye6225.Registration.Controller;

import com.amazonaws.AmazonServiceException;
import neu.coe.csye6225.Registration.Exception.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {
    @ExceptionHandler(EmailNotValidException.class)
    public ResponseEntity<String> emailNotValid(){
        return new ResponseEntity<>("Email is not valid", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> unauthorized(){
        return new ResponseEntity<>("Username or password is not correct", HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(EmailExistsException.class)
    public ResponseEntity<String> emailExists(){
        return new ResponseEntity<>("Email already exists", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PictureFormatNotSupportException.class)
    public ResponseEntity<String> pictureFormatNotSupport(){
        return new ResponseEntity<>("Picture Format Not Support", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(PictureNotFoundException.class)
    public ResponseEntity<String> pictureNotFound(){
        return new ResponseEntity<>("Picture Not Found", HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BadFormatException.class)
    public ResponseEntity<String> badFormat(){
        return new ResponseEntity<>("Body format is bad.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AmazonServiceException.class)
    public ResponseEntity<String> amazonServiceException(){
        return new ResponseEntity<>("Amazon Service Exception. Maybe forget to set up profile.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailNotVerifiedException.class)
    public ResponseEntity<String> emailNotVerifiedException(){
        return new ResponseEntity<>("Email address has not been verified. Please click the link to verify your email", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailLinkNotExpiredException.class)
    public ResponseEntity<String> emailLinkNotExpiredException(){
        return new ResponseEntity<>("The email link has not been expired. Please check your email or wait a minute to send another verification email", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailLinkExpiredException.class)
    public ResponseEntity<String> emailLinkExpiredException(){
        return new ResponseEntity<>("The email link has been expired. Please create the email again.", HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailTokenNotCorrectException.class)
    public ResponseEntity<String> emailTokenNotCorrectException(){
        return new ResponseEntity<>("The email token is not correct.", HttpStatus.BAD_REQUEST);
    }
}
