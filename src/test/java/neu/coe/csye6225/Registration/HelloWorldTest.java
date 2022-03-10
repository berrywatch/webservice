package neu.coe.csye6225.Registration;

import neu.coe.csye6225.Registration.Controller.HelloWorld;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HelloWorldTest {

    @Test
    void healthz() {
        HelloWorld c = new HelloWorld();
        String response = c.healthz();
        assertEquals("", response);
    }

    @Test
    void homeRequest() {
        HelloWorld c = new HelloWorld();
        String response = c.homeRequest();
        assertEquals("Hello World!", response);
    }
}