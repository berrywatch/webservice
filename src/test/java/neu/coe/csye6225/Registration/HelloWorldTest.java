package neu.coe.csye6225.Registration;

import neu.coe.csye6225.Registration.Controller.HelloWorld;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldTest {

    @Test
    void healthz() {
        HelloWorld c = new HelloWorld();
        String response = c.healthzTest();
        assertEquals("", response);
    }

    @Test
    void homeRequest() {
        HelloWorld c = new HelloWorld();
        String response = c.homeTest();
        assertEquals("Hello World!", response);
    }
}