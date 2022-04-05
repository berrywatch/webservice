package neu.coe.csye6225.Registration.Controller;

import neu.coe.csye6225.Registration.Util.StatsdClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class HelloWorld {
    @Autowired
    private StatsdClient statsdClient;

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    @GetMapping("/healthz")
    public String healthz() {
        statsdClient.increment("healthz.get");
        logger.info("healthz API being called");
        return "";
    }

    public String healthzTest(){
        return "";
    }

    public String homeTest(){
        return "Hello World!";
    }

    @GetMapping("")
    public String homeRequest(){
        statsdClient.increment("home.get");
        logger.info("home request API being called");
        return String.format("Hello World!");
    }
}
