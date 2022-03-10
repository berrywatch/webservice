package neu.coe.csye6225.Registration.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class HelloWorld {
    @GetMapping("/healthz")
    public String healthz() {
        return "";
    }

    @GetMapping("")
    public String homeRequest(){
        return String.format("Hello World!");
    }
}
