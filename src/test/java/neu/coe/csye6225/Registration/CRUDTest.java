package neu.coe.csye6225.Registration;

import neu.coe.csye6225.registration.CRUD;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CRUDTest {

    @Test
    void hello() {
        CRUD c = new CRUD();
        String response = c.hello("world!");
        assertEquals("Hello, world!", response);
    }

    @Test
    void postRequest() {
        CRUD c = new CRUD();
        String response = c.postRequest("Bob","body");
        assertEquals("Post received, Bob", response);
    }
}