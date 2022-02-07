package neu.coe.csye6225.registration;

import org.springframework.web.bind.annotation.*;

@RestController
public class CRUD {
    @GetMapping("/")
    public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
        return String.format("Get received with name %s", name);
    }

    @PostMapping("/")
    public String postRequest(@RequestParam(value="name", defaultValue = "post") String name, @RequestBody String body){
        System.out.println(name);
        System.out.println(body);
        return String.format("Post received.");
    }
}
