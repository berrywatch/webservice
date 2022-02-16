package neu.coe.csye6225.Registration;

import neu.coe.csye6225.Registration.Controller.HelloWorld;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldTest {

    @Test
    void hello() {
        HelloWorld c = new HelloWorld();
        String response = c.hello("world!");
        assertEquals("Hello, world!", response);
    }

    @Test
    void postRequest() {
        HelloWorld c = new HelloWorld();
        String response = c.postRequest("Bob","body");
        assertEquals("Post received, Bob", response);
    }
}