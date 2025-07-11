package de.tum.users.model;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;

class UserTest {
    @Test
    void testUserConstructorAndGetters() {
        User user = new User("ghid", "username", "token");
        user.setId("id123");
        user.setAnalysis(new ArrayList<>());
        assertEquals("ghid", user.getGithubId());
        assertEquals("username", user.getUsername());
        assertEquals("token", user.getToken());
        assertEquals("id123", user.getId());
        assertTrue(user.getAnalysis().isEmpty());
    }

    @Test
    void testSetters() {
        User user = new User("ghid", "username", "token");
        user.setId("id456");
        user.setToken("newtoken");
        assertEquals("id456", user.getId());
        assertEquals("newtoken", user.getToken());
    }
}
