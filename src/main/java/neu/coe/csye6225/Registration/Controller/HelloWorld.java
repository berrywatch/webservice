package neu.coe.csye6225.Registration.Controller;

import org.springframework.web.bind.annotation.*;

@RestController
public class HelloWorld {
    @GetMapping("/healthz")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Hello, %s", name);
    }

    @PostMapping("/")
    public String postRequest(@RequestParam(value="name", defaultValue = "post") String name, @RequestBody String body){
        return String.format("Post received, %s", name);
    }
}
